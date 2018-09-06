package todos;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * <pre>
 * {@code
 *
 *   # list all todos
 *   curl -v -H "Accept: application/json" http://localhost:8080/todomvc-javaee/resources/todos
 *
 *   # Create new todo
 *   curl -v -X POST -H "Content-type: application/json" -d '{ "title":"Test TODO", "completed":false }' http://localhost:8080/resources/todos
 *
 *   # show todo with id 1
 *   curl -v -H "Accept: application/json" http://localhost:8080/resources/todos/1
 *
 *   # delete todo with id 1
 *   curl -v -X DELETE http://localhost:8080/resources/todos/1
 *
 *   # search for all todo's with titles beginning with 'test'
 *   curl -v -d '{"title":"test"}' -H "Content-type: application/json" -H "Accept: application/json" http://localhost:8080/resources/todos/search
 *
 *   # search for all todo's with completed = true
 *   curl -v -d '{"title":"test"}' -H "Content-type: application/json" -H "Accept: application/json" http://localhost:8080/resources/todos/search
 * }
 * </pre>
 */
@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoResource {

    private final TodoService todos;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Todo todo, UriComponentsBuilder ucBuilder) {

        Todo saved = this.todos.save(todo);

        URI newLocation = ucBuilder.path("/todos/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(newLocation).build();
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(this.todos.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        Todo found = this.todos.getById(id);
        if (found == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(found);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Todo todo) {

        Todo updated = todos.update(id, todo);

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id) {

        if (todos.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(Todo example) {

        List<?> result = this.todos.findAllByExample(example);

        return ResponseEntity.ok(result);
    }
}
