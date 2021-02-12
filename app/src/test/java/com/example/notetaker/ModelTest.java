package com.example.notetaker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    Model m = new Model("1233","Test1","this is description");

    @Test
    void getId() { assertEquals("1233",m.getId()); }

    @Test
    void getTitle() {
        assertEquals("Test1",m.getTitle());
    }


    @Test
    void getDescription() {
        assertEquals("this is description",m.getDescription());
    }


}