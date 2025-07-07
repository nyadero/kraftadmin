package com.bowerzlabs.models;

import com.bowerzlabs.annotations.DisplayField;
import com.bowerzlabs.annotations.FormInputType;
import com.bowerzlabs.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity(name = "talents")
@Table(name = "talents")
public class Talent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Size(min = 3, max = 256, message = "Name should not be less than 3 characters long")
    private String name;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private List<String> hobbies;
    private LocalDate dob;
    @Column(length = 3000)
    @FormInputType(value = FormInputType.Type.TEXTAREA)
    private String about;
    private Boolean isHirable;
    private Integer age;
//    @FormInputType(FormInputType.Type.CURRENCY)
    private Double salary;
    private LocalTime timeAvailable;
    @Embedded
    @DisplayField("address")
    private Contact contact;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] introVideo;
    @FormInputType(FormInputType.Type.COLOR)
    private String favColor;
    @FormInputType(FormInputType.Type.IMAGE)
    private String avatar;
    @FormInputType(FormInputType.Type.FILE)
    private String resume;
    @FormInputType(FormInputType.Type.PASSWORD)
    private String password;
    @FormInputType(FormInputType.Type.EMAIL)
    private String email;
    @FormInputType(FormInputType.Type.TEL)
    private String phone;
    @FormInputType(FormInputType.Type.URL)
    private String url;

    @OneToMany(mappedBy = "talent", fetch = FetchType.EAGER)
    @DisplayField("title")
    private List<Project> projects;

//    @ElementCollection
//    private List<Experience> experiences;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public Talent(Long id, String name, Gender gender, List<String> hobbies, LocalDate dob, String about, Boolean isHirable, Integer age, Contact contact, Double salary, LocalTime timeAvailable, String favColor, String avatar, String resume, String password, String email, String phone, String url, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.hobbies = hobbies;
        this.dob = dob;
        this.about = about;
        this.isHirable = isHirable;
        this.age = age;
        this.salary = salary;
        this.timeAvailable = timeAvailable;
        this.favColor = favColor;
        this.avatar = avatar;
        this.resume = resume;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.url = url;
        this.contact = contact;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Talent() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Boolean getHirable() {
        return isHirable;
    }

    public void setHirable(Boolean hirable) {
        isHirable = hirable;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public LocalTime getTimeAvailable() {
        return timeAvailable;
    }

    public void setTimeAvailable(LocalTime timeAvailable) {
        this.timeAvailable = timeAvailable;
    }

    public String getFavColor() {
        return favColor;
    }

    public void setFavColor(String favColor) {
        this.favColor = favColor;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public byte[] getIntroVideo() {
        return introVideo;
    }

    public void setIntroVideo(byte[] introVideo) {
        this.introVideo = introVideo;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
