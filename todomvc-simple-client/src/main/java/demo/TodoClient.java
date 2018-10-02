package demo;

import java.util.List;

public interface TodoClient {

    List<Todo> findAll();

    Todo create(Todo todo);
}
