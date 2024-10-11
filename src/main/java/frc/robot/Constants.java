// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

 //Drive train motor IDs
 public static int leftPID = 2;
 public static int rightPID = 3;
 public static int leftFID = 4;
 public static int rightFID = 5;

 //Manipulator motor IDs
 public static int leftBaseID = 6;
 public static int rightBaseID = 7;
 public static int ampID = 8;
 public static int ampFollowID = 9;
 public static int intakeID = 10;

 //Manipulator Gyro ID
 public static int gyroID = 11;

 //Digital inputs DIO ports
 public static int intakeSensorID = 3;
 public static int frontSensorID = 1;
 public static int backSensorID = 2;

 //Manipulator Encoder Values
 //Note: Encoder values are in rotations, NOT DEGREES
 //original value: -10.5
 public static double shootPosition = -7.0;
 public static double supplyPosition = -15;
 public static double ampPosition = -30.0;
 public static double drivePosition = -25.0;
 public static double startPosition = -90.0;

 public static double shootAngle = 115;
 public static double resetAngle = 0;

//Manipulator Shooting Values
public static double shootSpeed = 0.70;
public static double supplySpeed = 0.75;
 
 //LED port (PWM)
 public static int ledPort = 0;
}
