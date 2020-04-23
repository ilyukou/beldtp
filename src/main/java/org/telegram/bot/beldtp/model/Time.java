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

    private long timeInMillis;

    private Integer year;

    private Byte day;

    private Byte month;

    private Byte hour;

    private Byte minute;

    public Time() {
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
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
        StringBuilder builder = new StringBuilder();

        if (hour == null) {
            builder.append("hh");
        } else {
            builder.append(hour);
        }

        builder.append(":");

        if (minute == null) {
            builder.append("mm");
        } else {
            builder.append(minute);
        }

        builder.append(" ");

        if (day == null) {
            builder.append("dd");
        } else {
            builder.append(day);
        }

        builder.append("/");

        if (month == null) {
            builder.append("mm");
        } else {
            builder.append(month + 1);
        }

        builder.append("/");

        if (year == null) {
            builder.append("yy");
        } else {
            builder.append(year);
        }

        return builder.toString();
    }
}
