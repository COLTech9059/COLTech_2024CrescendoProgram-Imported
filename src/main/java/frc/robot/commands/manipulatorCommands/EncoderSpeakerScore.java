package frc.robot.commands.manipulatorCommands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.Manipulator;

public class EncoderSpeakerScore extends Command
{

    private final Manipulator m_Manipulator;
    private boolean finished = false;
    private boolean didIPos = false;
    private boolean didSPos = false;
    private boolean didShoot = false;

    public EncoderSpeakerScore (Manipulator sentManip) 
    {
        m_Manipulator = sentManip;
        addRequirements(m_Manipulator);
    }

    @Override
    public void execute()
    {
        if (m_Manipulator.intakePosition(5, true) && !didIPos) didIPos = true;
        if (didIPos)
        {
            if (m_Manipulator.variablePosition(5, true, Constants.shootPosition) && !didSPos) didSPos = true;
            if (didSPos)
            {
                m_Manipulator.holdManipulator(true);
                m_Manipulator.runIntake(false, true);
                if (m_Manipulator.shootNote(true, Constants.shootSpeed) && !didShoot) didShoot = true;
                if (didShoot) finished = true;
                if (finished) m_Manipulator.runIntake(false, false);
            }
        }
    }

    @Override
    public boolean isFinished()
    {
        return finished;
    }

    @Override
    public void end(boolean interrupted)
    {
        finished = false;
        m_Manipulator.revUpFlywheel(false, 0);
        m_Manipulator.runIntake(false, false);
        m_Manipulator.holdManipulator(false);
        m_Manipulator.resetEncoders();
    }
}
