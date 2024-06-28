// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    // Apply common plugins here
    id("com.android.application") version "8.4.1" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.4.1")
        classpath("com.google.gms:google-services:4.4.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
