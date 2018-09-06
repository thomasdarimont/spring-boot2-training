package demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootApplication
public class SimpleMvcTypedViewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleMvcTypedViewsApplication.class, args);
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class Person {
    private String firstname;
    private String lastname;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "persons")
@XmlAccessorType(XmlAccessType.FIELD)
class PersonList implements Iterable<Person> {

    @XmlElement(name = "person")
    private List<Person> persons;

    @Override
    public Iterator<Person> iterator() {
        return persons.iterator();
    }
}

@Controller
class AppController {

    static final PersonList PEOPLE = new PersonList(Arrays.asList(new Person("Albert", "Einstein"), new Person("Nikola", "Tesla")));

    @GetMapping(path = "/data", produces = {MediaType.APPLICATION_XML_VALUE, "text/csv"})
    String getData(Model model) {
        model.addAttribute("data", PEOPLE);
        return "data";
    }
}


/**
 * <pre>
 *     curl -H "Accept: text/csv" -v localhost:8080/data
 *     curl -v localhost:8080/data\?format=csv
 * </pre>
 */
@Component("data.csv")
class CsvDataView extends AbstractView {

    public CsvDataView() {
        setContentType("text/csv");
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType(getContentType());
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), UTF_8))) {
            PersonList people = (PersonList) model.get("data");
            for (Person p : people) {
                out.println(p.getFirstname() + "," + p.getLastname());
            }
        }
        response.flushBuffer();
    }
}


/**
 * <pre>
 *     curl -H "Accept: application/xml" -v localhost:8080/data
 *     curl -v localhost:8080/data\?format=xml
 * </pre>
 */
@Component("data.xml")
class XmlDataView extends AbstractView {

    public XmlDataView() {
        setContentType(MediaType.APPLICATION_XML_VALUE);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType(getContentType());
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), UTF_8))) {
            PersonList people = (PersonList) model.get("data");
            JAXB.marshal(people, out);
        }
        response.flushBuffer();
    }
}