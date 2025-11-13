package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllComments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCommentById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/1"))
                .andExpect(status().isOk());
    }
}
