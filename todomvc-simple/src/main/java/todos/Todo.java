package todos;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Todo {

  @Id
  @GeneratedValue
  Long id;

  String title;

  Boolean completed;
}