package gradle;

import org.junit.jupiter.api.Test;

// import gradle.testProcessCommands.MockMainScreen;
// import gradle.testProcessCommands.MockQuestionHistory;

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
	
	class AppMediator {
	    MockMainScreen mainScreen;
	    MockQuestionHistory questionHistory;

	    public AppMediator(MockMainScreen mainScreen, MockQuestionHistory questionHistory) {
	        this.mainScreen = mainScreen;
	        this.questionHistory = questionHistory;
	    }

	    public void processVoiceCommand(String command) {
	        // Process the command
	        // Assume "delete" means we are clearing the screen and the history
	        if ("Delete prompt".equalsIgnoreCase(command)) {
	            mainScreen.removeAll();
	            questionHistory.clearAll();
	            mainScreen.revalidate();
	            mainScreen.repaint();
	        }
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
