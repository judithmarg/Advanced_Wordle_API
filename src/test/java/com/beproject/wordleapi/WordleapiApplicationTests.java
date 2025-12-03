package com.beproject.wordleapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    
    "spring.jpa.show-sql=false",
    
    "jwt.secret=1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF"
})
class WordleapiApplicationTests {

@Test
void contextLoads() {
}

}