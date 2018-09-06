package demo;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootApplication
public class AdvancedApiRateLimitingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvancedApiRateLimitingApplication.class, args);
    }
}

@RestController
@RequestMapping("/api")
class ApiController {

    @GetMapping("/data")
    public ResponseEntity<?> getData() {
        return ResponseEntity.ok().body(Map.of("data", System.currentTimeMillis()));
    }
}

@Component
@RequiredArgsConstructor
@WebFilter(urlPatterns = "${api.ratelimiting.path:/api/*}")
class RateLimitingInterceptor extends OncePerRequestFilter {

    @Value("${api.ratelimiting.enabled:true}")
    private boolean enabled;

    private final RateLimiter rateLimiter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (enabled && rateLimiter.limit(new HttpApiCall(request.getHeader("X-Client-Id"), request, response))) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit exceeded");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

interface RateLimiter {
    boolean limit(ApiCall apiCall);
}


interface ApiCall {
    String getClientId();

    String getApiName();

    void updateRateLimit(long limit, long remaining);

    void rateLimitExceeded(long secondsTillReset);
}

interface ApiUsage {
    long getMostRecentCallTimeMillis();

    void record();

    long getCount();

    void reset();

    boolean exceeds(ApiQuota quota);
}

interface ApiQuota {
    long getTimeWindowInMillis();

    long getRemaining(long count);

    long getLimit();
}

interface ApiUsageRepository {
    ApiUsage getUsage(ApiCall apiCall);

    void recordUsage(ApiCall apiCall);
}

interface ApiQuotaRepository {
    ApiQuota getQuota(ApiCall apiCall);
}

@Component
@RequiredArgsConstructor
class DefaultRateLimiter implements RateLimiter {

    private final ApiUsageRepository usages;

    private final ApiQuotaRepository quotas;

    @Override
    public boolean limit(ApiCall apiCall) {

        ApiUsage usage = usages.getUsage(apiCall);
        ApiQuota quota = quotas.getQuota(apiCall);

        if (shouldResetUsage(usage, quota)) {
            usage.reset();
        }

        apiCall.updateRateLimit(quota.getLimit(), quota.getRemaining(usage.getCount()));

        if (usage.exceeds(quota)) {
            apiCall.rateLimitExceeded(computeSecondsTillUsageReset(usage, quota));
            return true;
        }

        usages.recordUsage(apiCall);
        return false;
    }

    private long computeSecondsTillUsageReset(ApiUsage usage, ApiQuota quota) {
        return TimeUnit.MILLISECONDS.toSeconds(quota.getTimeWindowInMillis() - getMillisSinceLastCall(usage));
    }

    private boolean shouldResetUsage(ApiUsage usage, ApiQuota quota) {
        return getMillisSinceLastCall(usage) > quota.getTimeWindowInMillis();
    }

    private long getMillisSinceLastCall(ApiUsage usage) {
        return System.currentTimeMillis() - usage.getMostRecentCallTimeMillis();
    }
}

@Data
class HttpApiCall implements ApiCall {

    private final String clientId;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @Override
    public String getApiName() {
        return request.getRequestURI();
    }

    @Override
    public void updateRateLimit(long limit, long remaining) {
        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-Rate-Limit-Remaining", String.valueOf(remaining));
    }

    @Override
    public void rateLimitExceeded(long secondsTillReset) {
        response.setHeader("X-Rate-Limit-Reset", String.valueOf(secondsTillReset));
    }
}

@Data
class SimpleApiUsage implements ApiUsage {

    private final String apiName;

    private final AtomicLong counter = new AtomicLong();

    private long mostRecentCallTimeMillis = System.currentTimeMillis();

    public void record() {
        counter.incrementAndGet();
        mostRecentCallTimeMillis = System.currentTimeMillis();
    }

    public long getCount() {
        return counter.getAcquire();
    }

    public void reset() {
        counter.set(0L);
    }

    public boolean exceeds(ApiQuota quota) {
        return quota.getRemaining(getCount()) <= 0;
    }
}

@Data
class FixedApiQuota implements ApiQuota {

    private final long limit;

    private final long quotaPeriod;

    private final TimeUnit quotaPeriodTimeUnit;

    @Override
    public long getTimeWindowInMillis() {
        return quotaPeriodTimeUnit.toMillis(quotaPeriod);
    }

    @Override
    public long getRemaining(long count) {
        return getLimit() - count;
    }
}

@Component
class DefaultApiQuotaRepository implements ApiQuotaRepository {

    @Override
    public ApiQuota getQuota(ApiCall apiCall) {
        return new FixedApiQuota(5L, 10L, SECONDS);
    }
}

@Component
class InMemoryApiUsageRepository implements ApiUsageRepository {

    private final ConcurrentMap<String, ConcurrentMap<String, ApiUsage>> USAGES = new ConcurrentHashMap<>();

    @Override
    public ApiUsage getUsage(ApiCall apiCall) {
        return USAGES //
                .computeIfAbsent(apiCall.getClientId(), cid -> new ConcurrentHashMap<>()) //
                .computeIfAbsent(apiCall.getApiName(), SimpleApiUsage::new) //
                ;
    }

    @Override
    public void recordUsage(ApiCall apiCall) {
        getUsage(apiCall).record();
    }
}