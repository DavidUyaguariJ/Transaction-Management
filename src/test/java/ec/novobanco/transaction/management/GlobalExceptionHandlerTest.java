package ec.novobanco.transaction.management;

import ec.novobanco.transaction.management.exception.DomainException;
import ec.novobanco.transaction.management.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldHandleDomainException() throws Exception {
        mockMvc.perform(get("/test/domain-error")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Custom domain error")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.path", is("/test/domain-error")));
    }

    @Test
    void shouldHandleRuntimeException() throws Exception {
        mockMvc.perform(get("/test/runtime-error")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Internal server error")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.path", is("/test/runtime-error")));
    }

    @RestController
    private static class TestController {
        @GetMapping("/test/domain-error")
        public void throwDomainException() {
            throw new DomainException("Custom domain error");
        }

        @GetMapping("/test/runtime-error")
        public void throwRuntimeException() {
            throw new RuntimeException("Something went wrong");
        }
    }
}