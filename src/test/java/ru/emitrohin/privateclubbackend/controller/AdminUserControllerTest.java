package ru.emitrohin.privateclubbackend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.emitrohin.privateclubbackend.controller.admin.AdminUserController;
import ru.emitrohin.privateclubbackend.model.User;
import ru.emitrohin.privateclubbackend.service.UserService;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
public class AdminUserControllerTest {

    static final String URL = "/api/users/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET /api/users/:id возвращает 200 OK и пользователя")
    void shouldReturnUserById() throws Exception {
        final User user = new User();
        user.setId(UUID.fromString("f71fd70e-618d-46a2-ab77-d1b6da30b992"));
        UUID id = UUID.randomUUID();

    }

    @Test
    @DisplayName("GET /api/users/:id возвращает 404 NOT_FOUND")
    void shouldReturnNotFoundUserById() throws Exception {
        when(userService.findById(UUID.fromString("f71fd70e-618d-46a2-ab77-d1b6da30b992")))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/f71fd70e-618d-46a2-ab77-d1b6da30b992"))
                .andExpect(status().is(404))
                .andReturn();
    }

    @Test
    void shouldReturnEmptyUserList() throws Exception {

    }

    @Test
    void shouldReturnAllUsers() throws Exception {

    }

    @Test
    //TODO а как протестировать ошибки. Тут всегда будет 204 No content
    void shouldDeleteUserById() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(userService).deleteById(id);

        mockMvc.perform(delete("/" + id))
                .andExpect(status().isNoContent());
    }
}