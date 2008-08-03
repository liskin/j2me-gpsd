package org.barbelo;

import javax.microedition.lcdui.*;
import java.util.*;

/**
 * A stack of fullscreen widgets. This class implements a very simple GUI
 * logic.
 * (copypasted from middos, license does not matter)
 * @author Tomas Janousek
 */
public class GUIStack {
    private Stack stack = new Stack();
    private Display display;
    private Displayable root;

    /**
     * The only valid constructor.
     * @param display The display this GUIStack will be on.
     * @param root The root widget.
     */
    public GUIStack(Display display, Displayable root) {
	this.display = display;
	this.root = root;
	push(root, false);
    }

    /**
     * Push a widget and activate it.
     * @param screen The widget to push.
     * @param addBack Add the Back command automatically?
     */
    public synchronized void push(final Displayable screen, boolean addBack) {
	if (addBack) {
	    final Command backcmd = new Command("Back", Command.BACK, 1);
	    screen.addCommand(backcmd);
	    screen.setCommandListener(new CommandListener() {
		public void commandAction(Command c, Displayable d) {
		    if (d == screen && c == backcmd)
			pop();
		    else if (screen instanceof CommandListener)
			((CommandListener) screen).commandAction(c, d);
		}
	    });
	}

	stack.push(screen);
	display.setCurrent(screen);
    }

    public void push(final Displayable screen) {
	push(screen, true);
    }

    /**
     * Pop the topmost widget and possibly activate the one below it.
     * @param deferred Don't activate the one below.
     */
    public synchronized void pop(boolean deferred) {
	if (stack.peek() == root)
	    throw new EmptyStackException();
	stack.pop();
	if (!deferred)
	    activate();
    }

    public void pop() {
	pop(false);
    }

    /**
     * Pop the topmost widget, push another one and activate it.
     * @param screen The widget to push.
     * @param addBack Add the Back command automatically?
     */
    public synchronized void change(final Displayable screen, boolean addBack) {
	if (stack.peek() == root) {
	    root = screen;
	    addBack = false;
	}
	stack.pop();
	push(screen, addBack);
    }

    public void change(final Displayable screen) {
	change(screen, true);
    }

    /**
     * Pop all widgets above the specified one and activate it.
     */
    public synchronized void popTo(Displayable screen) {
	if (stack.search(screen) == -1)
	    return;

        while (stack.peek() != screen)
            stack.pop();
	activate();
    }
    
    /**
     * Pop all widgets expect the root one and activate it.
     */
    public synchronized void popAll() {
        while (stack.peek() != root)
            stack.pop();
	activate();
    }

    /**
     * Push an Alert.
     * @param alert The alert to push.
     */
    public void pushAlert(Alert alert) {
	alert.setCommandListener(new CommandListener() {
	    public void commandAction(Command c, Displayable d) {
		if (c == Alert.DISMISS_COMMAND) {
		    pop();
		}
	    }
	});
	push(alert, false);
    }

    /**
     * Activate the topmost widget.
     */
    public synchronized void activate() {
	display.setCurrent((Displayable) stack.peek());
    }

    /**
     * Minimize the stack.
     */
    public synchronized void hide() {
	display.setCurrent(null);
    }
}
