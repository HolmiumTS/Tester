package task;

/**
 * task type
 *
 * @author holmium
 * @see Task
 */
public enum TaskType {
    /**
     * full text compare mode
     * ignore space at line end
     *
     * @see test.Test
     */
    FULL_COMPARE,
    /**
     * special judge mode
     * check the answer with the checker defined by user
     *
     * @see test.SpecialJudge
     */
    SPECIAL_JUDGE,
    /**
     * communicate mode
     * the checker can communicate with paper's program dynamically
     *
     * @see test.Communicate
     */
    COMMUNICATE
}
