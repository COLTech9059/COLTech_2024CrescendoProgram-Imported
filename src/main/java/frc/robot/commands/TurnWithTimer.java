package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DriveTrain;

public class TurnWithTimer extends Command
{
    private DriveTrain drivetrain;

    private Timer turnTimer = new Timer();

    private double turnTime;
    private double turnPower;

    private boolean finished;

    public TurnWithTimer(DriveTrain dT, double turnPower, double turnTime)
    {
        drivetrain = dT;
        this.turnPower = turnPower;
        this.turnTime = turnTime;

        addRequirements(drivetrain);
    }

    @Override
    public void initialize()
    {
        turnTimer.start();
        drivetrain.drive(0, turnPower);
    }

    @Override
    public void execute()
    {
        if (turnTimer.get() >= turnTime) finished = true;
        else finished = false;
    }

    @Override
    public boolean isFinished()
    {
        return finished;
    }

    @Override
    public void end(boolean interrupted)
    {
        drivetrain.drive(0 ,0);
        turnTimer.reset();
        turnTimer.stop();
    }
}
