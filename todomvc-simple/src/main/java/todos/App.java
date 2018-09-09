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
        todos.save(Todo.newTodo("Vorlesung vorbereiten"));
        todos.save(Todo.newTodo("Vorlesung halten"));
        todos.save(Todo.newTodo("Feedback sammeln"));
        todos.save(Todo.newTodo("Vorlesung nachbereiten"));
    }
}
