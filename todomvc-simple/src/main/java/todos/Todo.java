package todos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer", "fieldHandler"})
public class Todo {

    @Id
    @GeneratedValue
    Long id;

    @NotNull
    String title;

    @NotNull
    Boolean completed = false;

    public static Todo newTodo(String title) {
        Todo todo = new Todo();
        todo.setTitle(title);
        return todo;
    }
}