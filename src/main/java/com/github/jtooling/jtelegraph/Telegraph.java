/*
 *   JTelegraph -- a Java message notification library
 *   Copyright (c) 2012, Paulo Roberto Massa Cereda, Antoine Neveux
 *   All rights reserved.
 *
 *   Redistribution and use in source and binary forms, with or without
 *   modification, are permitted provided that the following conditions
 *   are met:
 *
 *   1. Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 *   3. Neither the name of the project's author nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *   FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *   COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *   BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *   CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *   LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *   WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *   POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.jtooling.jtelegraph;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.pushingpixels.trident.Timeline;

import com.github.jtooling.jtelegraph.audio.AudioCallback;

/**
 * Packs the window and the animation configuration. Please dispose the object
 * if you don't add it to a queue.
 * 
 * @author Paulo Roberto Massa Cereda
 * @version 2.1
 * @since 2.0
 */
public class Telegraph {
	/**
	 * The message title
	 */
	private final String title;
	/**
	 * The message description
	 */
	private final String description;
	/**
	 * The {@link TelegraphConfig} configuration object to be used by this
	 * {@link Telegraph}
	 */
	private final TelegraphConfig config;
	/**
	 * The {@link TelegraphWindow} window that will be used to display the
	 * {@link Telegraph}
	 */
	private TelegraphWindow window;
	/**
	 * The intro {@link Timeline} which will be the first to be executed
	 */
	private Timeline timelineIntro;
	/**
	 * The stay {@link Timeline} which corresponds to the one where the message
	 * is shown
	 */
	private Timeline timelineStay;
	/**
	 * The away {@link Timeline} which is the one used while discarding the
	 * {@link Telegraph}
	 */
	private Timeline timelineAway;

	/**
	 * @param title
	 *            The telegraph title.
	 * @param description
	 *            The telegraph description.
	 */
	public Telegraph(final String title, final String description) {
		this.title = title;
		this.description = description;
		// Create and use a default configuration
		config = new TelegraphConfig();
		// Use it right now for this object
		configure();
	}

	/**
	 * @param title
	 *            The telegraph title.
	 * @param description
	 *            The telegraph description.
	 * @param config
	 *            The {@link TelegraphConfig} to be used by this
	 *            {@link Telegraph} object.
	 */
	public Telegraph(final String title, final String description,
			final TelegraphConfig config) {
		this.title = title;
		this.description = description;
		this.config = config;
		// Call the configuration of this object
		configure();
	}

	/**
	 * Allows to get the {@link TelegraphConfig} used by this {@link Telegraph}
	 * instance
	 * 
	 * @return The {@link TelegraphConfig} defined in the {@link #config} field
	 *         of this object
	 */
	public TelegraphConfig getConfig() {
		return config;
	}

	/**
	 * Checks if the animation is still running.
	 * 
	 * @return true if the animation is still running, false otherwise
	 */
	protected boolean isRunning() {
		// Check if Telegraph window has been disabled by button
		// Used to fix the bug of blocking button telegraph
		// Otherwise, check if all the timelines have been executed...
		return window != null && window.isDiscarded() ? false : !(timelineIntro
				.isDone() && timelineStay.isDone() && timelineAway.isDone());
	}

	/**
	 * Applies the configuration to the current {@link Telegraph} object.
	 */
	private void configure() {
		// Create a new telegraph window
		window = new TelegraphWindow(title, description, config);

		// Set the window height and width
		config.setWindowHeight(window.getHeight());
		config.setWindowWidth(window.getWidth());

		// Create the three timelines
		timelineIntro = new Timeline(window);
		timelineStay = new Timeline(window);
		timelineAway = new Timeline(window);

		// Configure the intro animation, when the window enters
		timelineIntro.addPropertyToInterpolate("position",
				config.getInitialCoordinates(), config.getFinalCoordinates());

		// Add the callback to the main timeline
		timelineIntro.addCallback(new AudioCallback(this, timelineStay));

		// If the window doesn't have a button
		if (!config.isButtonEnabled()) {
			// If there's a stop on mouse over
			if (config.isStoppedOnMouseOver())
				// Add a new listener
				window.addMouseListener(new MouseListener() {
					@Override
					public void mouseClicked(final MouseEvent e) {
					}

					@Override
					public void mousePressed(final MouseEvent e) {
					}

					@Override
					public void mouseReleased(final MouseEvent e) {
					}

					@Override
					public void mouseEntered(final MouseEvent e) {
						// If the window is in position
						if (timelineIntro.isDone() && !timelineStay.isDone())
							// Suspend animation
							timelineStay.suspend();
					}

					@Override
					public void mouseExited(final MouseEvent e) {
						// Window is still in position
						if (timelineIntro.isDone() && !timelineStay.isDone())
							// Resume animation
							timelineStay.resume();
					}
				});

			// Configure the time the window should wait in the screen
			timelineStay.setDuration(config.getDuration());
			timelineStay.addCallback(new SimpleCallback(timelineAway));
		}
		// Add duration
		timelineIntro.setDuration(config.getInDuration());

		// Configure the end animation, when the window goes away
		timelineAway.addPropertyToInterpolate("position",
				config.getFinalCoordinates(), config.getInitialCoordinates());
		timelineAway.setDuration(config.getOutDuration());
		timelineAway.addCallback(new EndCallback(window));

		// Set the last timeline
		window.setTimeline(timelineAway);
	}

	/**
	 * Plays the animation.
	 */
	protected void show() {
		timelineIntro.play();
	}

	/**
	 * Disposes the telegraph window. There's no need of calling this method,
	 * unless you don't add the telegraph to the queue.
	 */
	public void dispose() {
		// If there's still an object reference, dispose it
		if (window != null)
			window.dispose();
	}
}
