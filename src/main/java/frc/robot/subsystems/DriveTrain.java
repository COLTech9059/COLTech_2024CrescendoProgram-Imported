// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class DriveTrain extends SubsystemBase 
{
  /** Creates a new ExampleSubsystem. */

    // private Robot robot = new Robot();

    //create motor controller objects
    private CANSparkMax leftP = new CANSparkMax(2, MotorType.kBrushless);
    private CANSparkMax rightP = new CANSparkMax(3, MotorType.kBrushless);
    private CANSparkMax leftF = new CANSparkMax(4, MotorType.kBrushless);
    private CANSparkMax rightF = new CANSparkMax(5, MotorType.kBrushless);
  
    //create encoder objects
    public RelativeEncoder leftEncoder = leftP.getEncoder();
    public RelativeEncoder rightEncoder = rightP.getEncoder();
  
    // Create the differential drive object
    public final DifferentialDrive HamsterDrive = new DifferentialDrive(leftP, rightP);

  public DriveTrain() 
  {
    // Set up the motor controller followers
    leftF.follow(leftP);
    rightF.follow(rightP);
    
    // Invert the right side motor controller
    rightP.setInverted(true);

    //Disable the safety feature of the drivetrain, which can be very difficult to work around
    HamsterDrive.setSafetyEnabled(false);

    // Set deadband for the differential drive
    HamsterDrive.setDeadband(0.1);

    //Set the encoder positions to zero, effectively resetting them
    leftEncoder.setPosition(0);
    rightEncoder.setPosition(0);

    //Set the motors to accelerate and decelerate slower
    rightP.setOpenLoopRampRate(0.2);
    leftP.setOpenLoopRampRate(0.2);
  }

  //#STOPDRIVE
  //Method to stop the drive train
  public void stopDrive() 
  {
    HamsterDrive.arcadeDrive(0, 0, false);
  }

  Timer turnTimer = new Timer();

  public void drive(double forwardPow, double turnPow) 
  {
    HamsterDrive.arcadeDrive(forwardPow *.9, turnPow *-.8, false);
  }

  @Override
  public void periodic(){
    encoderMath();
  }

  public double rightWheelRotations = 0;
  public double leftWheelRotations = 0;

  public double rightDistance = 0;
  public double leftDistance = 0;

  public double avgEncoderDistance = 0;

  private ShuffleboardTab driveTab = Shuffleboard.getTab("Drive Tab");

  private GenericEntry encoderDistance =
      driveTab.add("Average Encoder Distance", avgEncoderDistance)
      .getEntry();

  //#ENCODERMATH
  //This function handles all of the math and data necessary to use the encoders
  public void encoderMath() 
  {
    //All the math to convert encoder rotations to horizontal distance in inches
    rightWheelRotations = rightEncoder.getPosition() / 8.45;
    leftWheelRotations = leftEncoder.getPosition() / 8.45;

    rightDistance = rightWheelRotations * 18;
    leftDistance = leftWheelRotations * 18;

    avgEncoderDistance = (rightDistance + leftDistance) / 2;
    encoderDistance.setDouble(avgEncoderDistance);
  }
}
