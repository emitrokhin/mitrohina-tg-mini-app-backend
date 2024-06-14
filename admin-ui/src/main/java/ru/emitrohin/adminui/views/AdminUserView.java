package ru.emitrohin.adminui.views;

import ru.emitrohin.data.model.AdminUser;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;

import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;

@PageTitle("Администраторы")
@Route(value = "admins", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminUserView extends Div implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }
}
