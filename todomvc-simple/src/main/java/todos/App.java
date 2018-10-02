package todos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    @Autowired
    public void init(TodoService todos) {
        todos.create(Todo.newTodo("Vorlesung vorbereiten"));
        todos.create(Todo.newTodo("Vorlesung halten"));
        todos.create(Todo.newTodo("Feedback sammeln"));
        todos.create(Todo.newTodo("Vorlesung nachbereiten"));
    }
}
