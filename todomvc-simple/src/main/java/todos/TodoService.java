package todos;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;
import static org.springframework.data.domain.ExampleMatcher.matching;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository repository;

    @Transactional
    public Todo save(Todo todo) {
        return repository.save(todo);
    }

    public Optional<Todo> getById(Long id) {
        return repository.findById(id);
    }

    public List<Todo> findAll() {
        return repository.findAll();
    }

    @Transactional
    public boolean deleteById(Long id) {
        return getById(id) //
                .map(todo -> {
                    repository.delete(todo);
                    return true;
                }) //
                .orElse(false);
    }

    @Transactional
    public Todo update(Long id, Todo newTodo) {
        return getById(id) //
                .map(savedTodo -> update(savedTodo, newTodo)) //
                .orElse(null);
    }

    private Todo update(Todo savedTodo, Todo newTodo) {

        if (newTodo.getCompleted() != null) {
            savedTodo.setCompleted(newTodo.getCompleted());
        }

        if (newTodo.getTitle() != null) {
            savedTodo.setTitle(newTodo.getTitle());
        }

        return repository.save(savedTodo);
    }

    public List<?> findAllByExample(Todo todo) {

        if (todo.getId() != null) {
            return getById(todo.getId())
                    .map(Collections::singletonList)
                    .orElseGet(Collections::emptyList);
        }

        Example<Todo> example = Example.of(todo, matching()
                .withMatcher("title", startsWith())
        );

        return repository.findAll(example);
    }
}
