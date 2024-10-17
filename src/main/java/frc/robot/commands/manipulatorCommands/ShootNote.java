package frc.robot.commands.manipulatorCommands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.subsystems.Manipulator;

public class ShootNote extends Command {
    private final Manipulator m_Manipulator;
    private final Timer moveTime = new Timer();
    private final double startTime;
    private final double endTime;
    private final double targetPos;
    private final double shootPower;
    private boolean Completed = false;
    
    public ShootNote(Manipulator manip, double targetPos, double shootPower, double start, double end){
        m_Manipulator = manip;
        startTime = start;
        endTime = end;
        this.targetPos = targetPos;
        this.shootPower = shootPower;

        addRequirements(m_Manipulator);
    }

    @Override
    public void initialize(){
        m_Manipulator.revUpFlywheel(true, shootPower);
        moveTime.start();
    }

    @Override
    public void execute(){
        if (m_Manipulator.variablePosition(5, true, targetPos))
        {
            if (moveTime.get() > startTime) m_Manipulator.runIntake(false, true, true);
        }
        if (moveTime.get() >= endTime)
        {
            m_Manipulator.runIntake(false, false, true);
            m_Manipulator.revUpFlywheel(false, 0);
            Completed = true;
        }
    }
    @Override
    public boolean isFinished(){
        return Completed;
    }
    @Override
    public void end(boolean interrupted){
        Completed = false;
        m_Manipulator.revUpFlywheel(false, 0);
        m_Manipulator.runIntake(false, false, true);
        m_Manipulator.moveArm(0.0);

        moveTime.stop();
        moveTime.reset();
    }
}
