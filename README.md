This is the COLTech robotics program for the 2024 game: Crescendo. 
This file will be used for all further pushes on our main program, as it has been imported into the 2024 version of WPIlib VSCode
To see commits on this program from before January 27th, 2024, go to our repository: COLTech_2024CrescendoProgram

Methods List:
(Spacing looks strange to make the constructors more easy to see)
  DriveTrain.Java Methods:
  
    //This method will stop all the motors on the drivetrain object
   public void stopDrive() {
    HamsterDrive.stopMotor();
    }

    //This method will automatically drive the robot to the specified distance (in inches) and at the specified speed; Testing has concluded that the margin of error on low         speeds is less than 3 inches
  public static void autoDrive(double speed, double distance) {
  if (rightDistance < distance) {
    HamsterDrive.arcadeDrive(speed, 0, false);
  } else if (rightDistance >= distance) {
    HamsterDrive.arcadeDrive(0, 0, false);
  }
  }

    //This method will reset all the motor controller, encoder, and other objects to their default settings and initialize them to the intended settings
  public static void resetDrive() {
  // Reset the factory defaults for the motor controllers
  leftP.restoreFactoryDefaults();
  rightP.restoreFactoryDefaults();
  leftF.restoreFactoryDefaults();
  rightF.restoreFactoryDefaults(); 

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
  }

    //This method will do all the math necessary to translate the raw encoder data into distance (in inches)
   public static void encoderMath() {
    //All the math to convert encoder rotations to horizontal distance in inches
    rightWheelRotations = rightEncoder.getPosition() / 8.45;
    leftWheelRotations = leftEncoder.getPosition() / 8.45;

   rightDistance = rightWheelRotations * 18;
    leftDistance = leftWheelRotations * 18;

  // Displays the Left and Right encoder rates on the dashboard with the specified names
    SmartDashboard.putNumber("Left Encoder Distance", leftDistance);
    SmartDashboard.putNumber("Right Encoder Distance", rightDistance);
     }

     //This method uses all the required inputs and does a little bit of math to calibrate the speed to a useable state
  public static void drive() {
      // Get the value of the Y-Axis on the joystick
    double forward = IO.dController.getLeftY();

  // Adjust Speed/Power so that it will always be at a max of 80%
    double change = 0;

   if (forward < 0) change = 0.2;
    if (forward > 0) change = -0.2;

   double forwardPower = forward + change;

  // Set turn to the value of the X-Axis on the joystick
    double turn = IO.dController.getRightX();

   // Reduce Turn Power
    double turnPower = turn *= 0.82;

   // Drive the Robot with <forwardPower> and <turnPower>
    if (IO.dController.getRightX() > 0.1 || IO.dController.getRightX() < -0.1 || IO.dController.getLeftY() > 0.1 || IO.dController.getLeftY() < -0.1) {
    HamsterDrive.arcadeDrive(forwardPower, turnPower);
    } else {
    HamsterDrive.arcadeDrive(0, 0, false);
    }
     }


  Manipulator.Java Methods:

      //This method resets all the manipulator objects and sets them to the intended settings
  public void initializeManipulator() {

  //Reset the digital sensors boolean
        usingDigitalSensors = true;

   //Reset intake boolean
        if (!beamSensor.get()) {didIntake = false;} else {didIntake = true;}

   //Reset the motors to their factory defaults
        leftBaseMotor.restoreFactoryDefaults();
        rightBaseMotor.restoreFactoryDefaults();
        ampMotor.restoreFactoryDefaults();
        rightIntakeMotor.restoreFactoryDefaults();
        leftIntakeMotor.restoreFactoryDefaults();

  //Set the leftIntakeMotor as a follower
        leftIntakeMotor.follow(rightIntakeMotor);
        leftIntakeMotor.setInverted(true);

   //Set the leftBaseMotor as a follower
        leftBaseMotor.follow(rightBaseMotor);
        leftBaseMotor.setInverted(true);

   //Set the encoders to 0, effectively resetting them
        leftBaseEncoder.setPosition(0);
        rightBaseEncoder.setPosition(0);
        ampEncoder.setPosition(0);
        rightIntakeEncoder.setPosition(0);
        leftIntakeEncoder.setPosition(0);
    }

      //This method will toggle the medthods between manual and digital sensor modes
  public void manualControl() {
        if (usingDigitalSensors) {usingDigitalSensors = false;} else {usingDigitalSensors = true;}
    }

      //This method will position the manipulator in a spot where it can score in the amp using digital sensors. It will stop after the sensor is triggered or the timeout is reached
  public void ampPosition(double timeout) {
        posTimer.reset();
        posTimer.start();
        if (posTimer.get() <= timeout) {
        if (!magneticSensor.get()) {
            rightBaseMotor.set(-0.3);
        } else if (magneticSensor.get()) {
            rightBaseMotor.set(0);
            rightBaseEncoder.setPosition(0);
            leftBaseEncoder.setPosition(0);

  didAmpPosition = true;
        }
    } else {
        rightBaseMotor.set(0);
        didAmpPosition = false;
    }
    }

      //This method will position the manipulator in a spot where it can shoot a note using digital sensors. It will stop once the encoders read a specified value or the timeout is triggered
  public void shootPosition(double timeout) {
        shootPosTime.reset();
        shootPosTime.start();
        if (shootPosTime.get() <= timeout) {
        if (rightBaseEncoder.getPosition() <= Constants.shootPosition) {
            rightBaseMotor.set(0.3);
        } else {
            rightBaseMotor.set(0);
            didShootPosition = true;
        }
    } else {
        rightBaseMotor.set(0);
        didShootPosition = false;
    }
    }

      //This method updates all the necessary values to the dashboard
  public void manipulatorDashboard() {
            //Push the digital sensor data to the shuffleboard
            SmartDashboard.putBoolean("Beam Sensor", beamSensor.get());
            SmartDashboard.putBoolean("Magnetic Sensor", magneticSensor.get());
        }

        //This method runs the intake using digital sensors. It will stop when the beam sensor is triggered or the timeout is reached
  public static void intake(double timeout) {
            intakeTime.reset();
            intakeTime.start();
        if (intakeTime.get() <= timeout) {
            if (!beamSensor.get()) {
                rightIntakeMotor.set(0.4);
            } else {
                rightIntakeMotor.set(0);
                didIntake = true;
            }
        } else {
            rightIntakeMotor.set(0);
            didIntake = false;
        }
        }

        //This method runs the intake manually, and stops once the left trigger is no longer being pressed
  public void intake() {
            if (IO.dController.getLeftTriggerAxis() > 0.4) {rightIntakeMotor.set(0.4);} else {rightIntakeMotor.set(0);} 
        }

        //This method uses digital sensor to shoot a note. It will stop once the beam sensor has returned false for at least 1 second or the timeout is reached
  public void shootNote(double timeout) {

  shootTimeout.reset();
            shootTimeout.start();
            shootTime.reset();

  if (shootTimeout.get() <= timeout) {
            //If the beam sensor is active, the intake motor runs in reverse until the beam sensor is deactivated,
            // at which point the intake motor will stop and the amp motor will run for 1 second at full power to shoot
            if (beamSensor.get()) {
                rightIntakeMotor.set(0.4);
            } else if (!beamSensor.get()) {
                shootTime.start();
                rightIntakeMotor.set(0);
                ampMotor.set(1);
            } 
            if (!beamSensor.get() && shootTime.get() >= 1) {
                ampMotor.set(0);
                shootTime.stop();
            }
        }
        }

        //This method shoots a note manually. It stops when the right trigger is no longer being pressed
  public void shootNote() {
            if (IO.dController.getRightTriggerAxis() > 0.4) {
                rightIntakeMotor.set(0.4);
                ampMotor.set(1);
            } else {
                rightIntakeMotor.set(0);
                ampMotor.set(0);
            }
        }

        //This method scores a note in the amp using digital sensors. It stops when the beam sensor has returned false for at least 1.5 seconds or the timeout is reached.
  public void ampScore(double timeout) {

  ampTimeout.reset();
            ampTimeout.start();
            ampTime.reset();

  if (ampTimeout.get() <= timeout) {
            //If the beam sensor is active, the intake motor runs in reverse until the beam sensor is deactivated,
            // at which point the intake motor will stop and the amp motor will run for 1.5 seconds at 40% power to score
            if (beamSensor.get()) {
                rightIntakeMotor.set(0.4);
            } else if (!beamSensor.get()) {
                ampTime.start();
                rightIntakeMotor.set(0);
                ampMotor.set(0.3);
            } 
            if (!beamSensor.get() && ampTime.get() >= 1.5) {
                ampMotor.set(0);
                ampTime.stop();
            }
        }
        }

        //This method manually scores in the amp. It ends when the B button is no longer held
  public void ampScore() {
            if (IO.dController.getBButtonPressed()) {
                rightIntakeMotor.set(0.4);
                ampMotor.set(0.3);
            } else if (IO.dController.getBButtonReleased()) {
                rightIntakeMotor.set(0);
                ampMotor.set(0);
            }
        }

        //This method will move the manipulator forward or backward, depending on the input
  public void moveManipulator(boolean isNegative) {
        if (!isNegative) {   
            if (IO.dController.getRightTriggerAxis() > 0.4) {
                rightBaseMotor.set(0.3);
            } else {
                rightBaseMotor.set(0);
            }
        } else {
            if (IO.dController.getLeftTriggerAxis() > 0.4) {
                rightBaseMotor.set(-0.3);
            } else {
                rightBaseMotor.set(0);
            }
        }
        }

        //This method moves the manipulator for a set amount of time in either forward or reverse
  public void moveManipulator(double moveTime, boolean isNegative) {
            if (!isNegative) {
            Timer moveTimer = new Timer();
            moveTimer.reset();
            moveTimer.start();
            if (moveTimer.get() <= moveTime) {
                rightBaseMotor.set(0.3);
            } else {
                rightBaseMotor.set(0);
            }
        } else {
                Timer moveTimer2 = new Timer();
                moveTimer2.reset();
                moveTimer2.start();
            if (moveTimer2.get() <= moveTime) {
                rightBaseMotor.set(-0.3);
            } else {
                rightBaseMotor.set(0);
            }
        }
        }

        //This method assigns all of the previous methods to specific buttons
  public void controlManipulator() {
            if (IO.dController.getAButton()) manualControl();
        if (usingDigitalSensors) {
            if (IO.dController.getLeftTriggerAxis() > 0.4) intake(4);
            if (IO.dController.getRightBumper()) moveManipulator(false);
            if (IO.dController.getLeftBumper()) moveManipulator(true);
            if (IO.dController.getYButton()) ampPosition(5);
            if (IO.dController.getXButton()) shootPosition(4);
            if (IO.dController.getBButton()) ampScore(4);
            if (IO.dController.getRightTriggerAxis() > 0.4) shootNote(3);
        } else {
            if (IO.dController.getRightBumper()) moveManipulator(false);
            if (IO.dController.getLeftBumper()) moveManipulator(true);
            if (IO.dController.getRightTriggerAxis() > 0.4) shootNote();
            if (IO.dController.getLeftTriggerAxis() > 0.4) intake();
            if (IO.dController.getBButton()) ampScore();
        }
        }

        //This method will automatically do a series of manipulator methods depending on the inputs
  public void autoManipulator(boolean doesIntake, boolean doesAim, boolean doesShoot, boolean doesAmp) {
            if (doesIntake) {
                moveManipulator(1.5, false);
                intake(4);
            }
            if (doesAim) ampPosition(5);
            if (doesShoot) shootNote(3);
            if (doesAmp) ampScore(4);
        }


        Limelight.java Methods:

        //This method will estimate the robot's distance from the target by triangulation (uses the height of the target and angle of the limelight)
  public double estimateDist(){
        int tempID = (int) curTargetID;
        if (tempID > 0.0){
            targetHeight = heightArray[tempID];
        }
        double radAngle = Math.toRadians(this.currentY + LensAngleFromMount);

   //Simple trigonometry to return distance from given angle 
        double distFromGoal = ((targetHeight - LensDistFromGround) / Math.tan(radAngle));
        estimDist = distFromGoal;
        return distFromGoal;
    }

      //This method will forcibly stop all of the functionality of the limelight
  public void stop(){
        seekTimer.stop();
        seekTimer.reset();
        driveTimer.stop();
        driveTimer.reset();
        refreshTimer.stop();
        refreshTimer.reset();
        enabled = false;
        targetFound = false;
        inPosition = false;
    }

    //This method will start up the limelight, allowing it to be used
  public void start(){
        enabled = true;
        targetFound = false;
        inPosition = false;
    }

    //This method will drive the robot to a certain range of distances based on the target area. Used for following a moving target (Testing only)
  public void getInRangeUsingArea(DriveTrain driveTrain){
        if(driveTimer.get() > 3.0 && seesTarget == 0.0){
            driveTrain.HamsterDrive.arcadeDrive(0, 0);
            stop();
        }
         if (enabled){
            if (driveTimer.get() == 0.0 && targetFound) {driveTimer.start(); refreshTimer.start();}
            if (driveTimer.get() > 0.0){
                if (currentArea <= 15.0){
                    double speed = -.35;
                    driveTrain.HamsterDrive.arcadeDrive(speed, 0);
                } else {
                    driveTrain.HamsterDrive.arcadeDrive(0, 0);
                    stop();
                }
            }
        }  
    }

    //This method will drive the robot to a certain range of distances through triangulation. Used for moving towards a stationary target (Used during matches)
   private double speed = .45;
    public void getInRangeUsingDistance(DriveTrain driveTrain){
        if(driveTimer.get() > 3.0 && refreshTimer.get() > 3.0){
            driveTrain.HamsterDrive.arcadeDrive(0, 0);
            stop();
        }
        if (enabled){
            if (driveTimer.get() == 0.0 && targetFound) {driveTimer.start(); refreshTimer.start();}
            if (driveTimer.get() > 0.0){
                if(seesTarget == 1.0){
                    refreshTimer.reset();
                    refreshTimer.start();
                }
                //Estimate distance from target and the distance error.
                double currentDist = estimateDist();
                double distError = desiredDist - currentDist; //Distance from desired point. Calculated in Inches.
                if (distError > 2.5 || distError < -2.5){
                    //Calculate driving adjust percentage for turning.
                    double drivingAdjust  = ((correctionMod * distError) * .1); //% of angle (i think)
                    //Cap the speed at 45% and set the floor at 25%
                    if (drivingAdjust > 0) speed = -.45;
                    else if (drivingAdjust < 0) speed = .45;
                    // if (drivingAdjust < .325 && drivingAdjust > 0.0) speed = .325;
                    // else if (drivingAdjust > -.325 && drivingAdjust < 0.0) speed = -.325;
                    //Cap turn power at 35% of value
                    double turnPower = -Math.pow((this.currentX*.1), 3);
                    if (turnPower < -.35)
                        turnPower = -.35;
                    else if (turnPower > .35)
                        turnPower = .35;
                    showTurnPower = turnPower;
                    driveTrain.HamsterDrive.arcadeDrive(speed, turnPower);
                } else {
                    driveTrain.HamsterDrive.arcadeDrive(0, 0);
                    stop();
                }
            }
        }  
    }

    //This method will align the limelight with the nearest april tag within a certain range of values (horizontal offset from target center)
  private double steeringPow = .3;
    public void seekTarget(DriveTrain driveTrain){
        if (seekTimer.get() > 10.0 && seesTarget == 0.0){
            driveTrain.HamsterDrive.arcadeDrive(0, 0);
            stop();
        }
        if (enabled) {
           if (seekTimer.get() <= 0.0 && !targetFound) seekTimer.start();
           if (seekTimer.get() > 0.0 && !targetFound){
            //If target isn't in view, set steeringPow to be a consistent .3. 
                if (seesTarget == 0.0){
                    steeringPow = .35;
                    driveTrain.HamsterDrive.arcadeDrive(0, steeringPow);
                } else {
                    //Else if it is visible then...
                    //Runs if it is not in the threshold.
                    if ((currentX > 5.0 || currentX < -5.0) && seesTarget != 0.0){
                        if (currentX > 0.0) steeringPow = -.35;
                        else if (currentX < 0.0) steeringPow = .35;
                        driveTrain.HamsterDrive.arcadeDrive(0, steeringPow);
                    } else if ((currentX < 5 && currentX > -5) && seesTarget != 0.0){
                        //We have found the target. Stop turning.
                        driveTrain.HamsterDrive.arcadeDrive(0, 0);
                        seekTimer.stop();
                        seekTimer.reset();
                        targetFound = true;
                    }
                }
           }
        }
    }

    //This method will post all of the limelight related values to the dashboard
   public void postValues(){
        //Get values from the limelight.
        currentX = tx.getDouble(0.0);
        currentY = ty.getDouble(0.0);
        currentArea = ta.getDouble(0.0);
        seesTarget = tv.getDouble(0.0);
        curTargetID = tid.getDouble(0.0);
        //Post SmartDashboard values
        SmartDashboard.putNumber("LimelightX", this.currentX);
        SmartDashboard.putNumber("LimelightY", this.currentY);
        SmartDashboard.putNumber("LimelightArea", this.currentArea);
        SmartDashboard.putNumber("LimeLightSeesTarget", this.seesTarget);
        SmartDashboard.putNumber("DistFromTarget", estimDist);
        SmartDashboard.putNumber("VisibleTargetID", this.curTargetID);
        SmartDashboard.putNumber("TurnPowerAdjust", showTurnPower);
    }

    //This method will activate the seekTarget() and getInRangeUsingDistance methods. Used to activate limelight targeting during Teleop
  public void activateLimelight () {
        seekTarget(drivetrain);
        getInRangeUsingDistance(drivetrain);
    }

    Robot.java methods:

    //This method follows a specific set of methods based on the input. Used for autonomous period
  public static void autoMode(int autoSelector) {
    if (autoSelector == 1) {
      manipulator.autoManipulator(false, true, true, false);
      DriveTrain.autoDrive(0.3, 24);
      manipulator.autoManipulator(true, true, false, false);
      DriveTrain.autoDrive(0.3, 24);
      manipulator.autoManipulator(false, true, true, false);
      DriveTrain.autoDrive(0.3, 36);
    }
  }
