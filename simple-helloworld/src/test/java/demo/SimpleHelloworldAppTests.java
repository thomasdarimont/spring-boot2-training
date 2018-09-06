package demo;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class SimpleHelloworldAppTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnPersonalizedGreeting() throws Exception {

        this.mockMvc //
                .perform(get("/").param("name", "Tester")) //
                .andExpect(status().isOk()) //
                .andExpect(content().string(Matchers.containsString("Hello Tester")))//
        ;
    }

    @Test
    public void shouldReturnDefaultGreeting() throws Exception {

        this.mockMvc //
                .perform(get("/")) //
                .andExpect(status().isOk()) //
                .andExpect(content().string(Matchers.containsString("Hello World")))//
        ;
    }

}
