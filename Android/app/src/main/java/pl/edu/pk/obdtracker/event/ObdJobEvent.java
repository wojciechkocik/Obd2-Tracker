package pl.edu.pk.obdtracker.event;

import lombok.Getter;
import lombok.Setter;
import pl.edu.pk.obdtracker.obd.ObdCommandJob;

/**
 * @author Wojciech Kocik
 * @since 13.03.2017
 */

public class ObdJobEvent {

    @Getter
    @Setter
    ObdCommandJob obdCommandJob;
}
