package demo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class SimpleCustomMvcRoutesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleCustomMvcRoutesApplication.class, args);
    }
}

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/documents")
class DocumentsController {

    private final DocumentService documentService;

    @GetMapping
    public Object getDocuments() {
        return documentService.getDocuments();
    }
}


@RestController
@RequestMapping("/api/my/documents")
class MyDocumentsController extends DocumentsController {

    public MyDocumentsController(@ContextAware DocumentService documentService) {
        super(documentService);
    }
}

interface DocumentService {
    List<String> getDocuments();

    List<String> getDocumentsByEmployeeId(String currentEmployeeId);
}

@Primary
@Service
class DefaultDocumentService implements DocumentService {

    @Override
    public List<String> getDocumentsByEmployeeId(String currentEmployeeId) {
        return getDocuments().stream().filter(d -> d.endsWith(currentEmployeeId)).collect(Collectors.toList());
    }

    @Override
    public List<String> getDocuments() {
        return Arrays.asList("doc1", "doc2");
    }
}

@ContextAware
@Service
@RequiredArgsConstructor
class MyDocumentService implements DocumentService {

    private final DocumentService documentService;

    private final CurrentEmployeeProvider currentEmployeeProvider;

    @Override
    public List<String> getDocuments() {
        return documentService.getDocumentsByEmployeeId(currentEmployeeProvider.getCurrentEmployeeId());
    }

    @Override
    public List<String> getDocumentsByEmployeeId(String currentEmployeeId) {
        throw new UnsupportedOperationException();
    }
}

@Qualifier
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@interface ContextAware {
}

@Component
class CurrentEmployeeProvider {

    public String getCurrentEmployeeId() {
        return "1";
    }
}