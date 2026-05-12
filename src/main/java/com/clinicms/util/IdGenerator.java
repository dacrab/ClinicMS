package com.clinicms.util;

import java.util.concurrent.atomic.AtomicInteger;

/** Αυτοματη δημιουργια μοναδικων IDs ανα τυπο οντοτητας (thread-safe). */
public final class IdGenerator {

    private static final AtomicInteger doctorCounter = new AtomicInteger(0);
    private static final AtomicInteger patientCounter = new AtomicInteger(0);
    private static final AtomicInteger appointmentCounter = new AtomicInteger(0);

    public static int nextDoctorId() { return doctorCounter.incrementAndGet(); }
    public static int nextPatientId() { return patientCounter.incrementAndGet(); }
    public static int nextAppointmentId() { return appointmentCounter.incrementAndGet(); }

    public static void seedDoctorId(int max) { doctorCounter.set(Math.max(doctorCounter.get(), max)); }
    public static void seedPatientId(int max) { patientCounter.set(Math.max(patientCounter.get(), max)); }
    public static void seedAppointmentId(int max) { appointmentCounter.set(Math.max(appointmentCounter.get(), max)); }

    private IdGenerator() {}
}
