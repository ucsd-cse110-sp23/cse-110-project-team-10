package gradle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;


public class testProcessCommands {
	
	class MockMainScreen {
	    boolean removeAllCalled = false;
	    boolean revalidateCalled = false;
	    boolean repaintCalled = false;

	    public void removeAll() {
	        removeAllCalled = true;
	    }

	    public void revalidate() {
	        revalidateCalled = true;
	    }

	    public void repaint() {
	        repaintCalled = true;
	    }
	}

	class MockQuestionHistory {
	    boolean clearAllCalled = false;

	    public void clearAll() {
	        clearAllCalled = true;
	    }
	}
	
    @Test
    public void testProcessVoiceCommand() {
        // Setup the mocks
        MockMainScreen mockMainScreen = new MockMainScreen();
        MockQuestionHistory mockQuestionHistory = new MockQuestionHistory();

        // Create the mediator
        AppMediator mediator = new AppMediator(mockMainScreen, mockQuestionHistory);

        // Call the method to test
        mediator.processVoiceCommand("Delete prompt");

        // Verify the correct methods were called
        assertTrue(mockMainScreen.removeAllCalled);
        assertTrue(mockMainScreen.revalidateCalled);
        assertTrue(mockMainScreen.repaintCalled);
        assertTrue(mockQuestionHistory.clearAllCalled);
    }
}
