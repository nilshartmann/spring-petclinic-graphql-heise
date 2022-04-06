package org.springframework.samples.petclinic.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.WebGraphQlTester;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureHttpGraphQlTester
public class OwnerControllerTests {
    @Autowired
    WebGraphQlTester graphQlTester;

    @Test
    void queryAllOwnersWorks() {
        // language=GraphQL
        String document = """
            query {
                owners {
                    owners {
                        id
                        firstName
                    }
                }
                }
            """;

        graphQlTester.mutate()
            .headers(httpHeaders -> httpHeaders.setBearerAuth("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2UiLCJpYXQiOjE2MDg4ODk0NDAsImV4cCI6MjM2NjI3MTg0MH0.V36ynhDffqb9LQFsckOdk6lFhcVEDhOCFxFCQDAYG0o"))
            .build()

            .document(document)
            .execute()
            .path("owners.owners[0].id").entity(String.class).isEqualTo("1")
            .path("owners.owners[0].firstName").entity(String.class).isEqualTo("George")
            ;

    }

}
