package com.example.demo.model;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;

public @Data class Nurse implements Serializable {

	private int id;

	private String firstName;

	private String lastName;

	private int age;

	private String gender;

	private double salary;

	private String email;

	private LocalDate startDate;

	private NurseAddress nurseAddress;

	private String departments;

}
