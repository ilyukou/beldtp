package org.telegram.bot.beldtp.model;

import javax.persistence.*;

@Entity
@Table(name = "time")
public class Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "time")
    private Incident incident;

    private Integer year;

    private Byte day;

    private Byte month;

    private Byte hour;

    private Byte minute;

    public Time() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Byte getDay() {
        return day;
    }

    public void setDay(Byte day) {
        this.day = day;
    }

    public Byte getMonth() {
        return month;
    }

    public void setMonth(Byte month) {
        this.month = month;
    }

    public Byte getHour() {
        return hour;
    }

    public void setHour(Byte hour) {
        this.hour = hour;
    }

    public Byte getMinute() {
        return minute;
    }

    public void setMinute(Byte minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(hour).append(":").append(minute)
                .append(" ")
                .append(day).append("/").append(month + 1).append("/").append(year)
                .toString();
    }
}
