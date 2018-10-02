package demo;


public class SimpleTodoClientApp {

    public static void main(String[] args) {

        TodoClient todoClient = new SimpleTodoClient("http://localhost:8090");

        System.out.println("ToDos before adding new ToDo");

        for (Todo todo : todoClient.findAll()) {
            System.out.println(todo);
        }

        System.out.println("Adding new ToDo");
        todoClient.create(new Todo("Todo mit RestTemplate anlegen"));

        System.out.println("ToDos after adding new ToDo");

        for (Todo todo : todoClient.findAll()) {
            System.out.println(todo);
        }
    }
}
