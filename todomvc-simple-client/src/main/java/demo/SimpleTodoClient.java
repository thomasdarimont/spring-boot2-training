package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Collections;
import java.util.List;

class SimpleTodoClient implements TodoClient {

    private static final String TODOS_ENDPOINT = "/todos";

    private final RestTemplate restTemplate;

    public SimpleTodoClient(String baseUrl) {
        this.restTemplate = createRestTemplate(baseUrl);
    }

    private RestTemplate createRestTemplate(String baseUrl) {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter(new ObjectMapper())));
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));

        return restTemplate;
    }

    @Override
    public List<Todo> findAll() {

        ResponseEntity<List<Todo>> response = restTemplate.exchange(
                TODOS_ENDPOINT, HttpMethod.GET, null, new ParameterizedTypeReference<List<Todo>>() {
                });

        return response.getBody();
    }

    @Override
    public Todo create(Todo todo) {

        ResponseEntity<Todo> response = restTemplate.postForEntity(TODOS_ENDPOINT, todo, Todo.class);

        return response.getBody();
    }
}
