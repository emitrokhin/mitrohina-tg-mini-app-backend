package ru.emitrohin.privateclubbackend.config;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.emitrohin.privateclubbackend.model.*;
import ru.emitrohin.privateclubbackend.repository.MaterialRepository;
import ru.emitrohin.privateclubbackend.repository.CourseRepository;
import ru.emitrohin.privateclubbackend.repository.TopicRepository;
import ru.emitrohin.privateclubbackend.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Configuration
@Profile("preload")
public class PreloadDatabase {
    private static final Logger log = LoggerFactory.getLogger(PreloadDatabase.class);
    private static final Faker ruFaker = new Faker(new Locale("ru"));
    private static final Faker enFaker = new Faker();
    private static final Random random = new Random();

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   CourseRepository courseRepository,
                                   TopicRepository topicRepository,
                                   MaterialRepository materialRepository) {

        return args -> {
            initUsers(userRepository);
            initCourse(courseRepository, topicRepository, materialRepository);
        };
    }

     private void initUsers(UserRepository repository) {
      for (int i = 0; i < 1 + Math.abs(random.nextInt(150)); i++) {
            LocalDateTime createdAt = LocalDateTime.now().minusDays(random.nextInt(365));  // Случайная дата посещения в прошлом году
            LocalDateTime lastVisit = LocalDateTime.now(ZoneOffset.UTC).minusDays(random.nextInt(365));  // Случайная дата посещения в прошлом году
            User user = new User();
            user.setTelegramId(Math.abs(random.nextLong()));
            user.setFirstName(ruFaker.name().firstName());
            user.setLastName(ruFaker.name().lastName());
            user.setUsername(enFaker.name().username());
            user.setPhotoUrl(enFaker.internet().url());
            user.setRole(Role.USER);
            user.setCreatedAt(createdAt);
            user.setLastVisit(lastVisit);

            log.info("Preloading user {}", repository.save(user));
        }
    }

    private void initCourse(CourseRepository courseRepository,
                            TopicRepository topicRepository,
                            MaterialRepository materialRepository) {
        for (int i = 0; i < 1 + Math.abs(random.nextInt(10)); i++) {

            Course course = new Course();
            course.setTitle(ruFaker.educator().course());
            course.setDescription(ruFaker.shakespeare().romeoAndJulietQuote());
            course.setCoverObjectKey(UUID.randomUUID().toString());

            Course saved = courseRepository.save(course);
            log.info("Preloading course {}", course);

            initCourseTopics(saved, topicRepository, materialRepository);
        }
    }

    private void initCourseTopics(Course course,
                                  TopicRepository topicRepository,
                                  MaterialRepository materialRepository) {
        for (int i = 0; i < Math.abs(1 +random.nextInt(10)); i++) {
            Topic topic = new Topic();
            topic.setTitle(ruFaker.educator().course());
            topic.setDescription(ruFaker.shakespeare().romeoAndJulietQuote());
            topic.setCoverObjectKey(UUID.randomUUID().toString());
            topic.setCourse(course);

            Topic saved = topicRepository.save(topic);
            log.info("Preloading topic {}", saved);

            initCourseData(saved, materialRepository);
        }

    }
    private void initCourseData(Topic topic, MaterialRepository repository) {
        for (int i = 0; i < 1 + Math.abs(random.nextInt(20)); i++) {
            Material material = new Material();
            material.setTitle(ruFaker.educator().course());
            material.setCoverObjectKey(UUID.randomUUID().toString());
            material.setMediaObjectKey(UUID.randomUUID().toString());
            material.setDuration(new Random().nextInt(10) + 1);
            material.setTopic(topic);

            log.info("Preloading data {}", repository.save(material));
        }
    }
}