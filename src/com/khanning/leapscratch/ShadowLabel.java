/*****************************************************************************
 *   This file is part of LeapScratch.                                       *
 *                                                                           *
 *   LeapScratch is free software: you can redistribute it and/or modify     *
 *   it under the terms of the GNU General Public License as published by    *
 *   the Free Software Foundation, either version 3 of the License, or       *
 *   (at your option) any later version.                                     *
 *                                                                           *
 *   LeapScratch is distributed in the hope that it will be useful,          *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 *   GNU General Public License for more details.                            *
 *                                                                           *
 *   You should have received a copy of the GNU General Public License       *
 *   along with LeapScratch.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                           *
 *****************************************************************************/

package com.khanning.leapscratch;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;

public class ShadowLabel extends JLabel {

	private static final long serialVersionUID = 1L;
	private int shadow_x, shadow_y;
	private Color color;

	public ShadowLabel(String text, int shadow_x, int shadow_y, Color color) {
		super(text);
		this.shadow_x = shadow_x;
		this.shadow_y = shadow_y;
		this.color = color;
	}

	public Dimension getPreferredSize() {
		String text = getText();
		FontMetrics fm = this.getFontMetrics(getFont());

		int w = fm.stringWidth(text);
		w += shadow_x;

		int h = fm.getHeight();
		h += shadow_y;

		return new Dimension(w, h);
	}

	public void paintComponent(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		char[] chars = getText().toCharArray();

		FontMetrics fm = this.getFontMetrics(getFont());
		int h = fm.getAscent();
		g.setFont(getFont());

		int x = 0;

		for (int i = 0; i < chars.length; i++) {
			char ch = chars[i];
			int w = fm.charWidth(ch);

			g.setColor(color);
			g.drawString("" + chars[i], x + shadow_x, h + shadow_y);

			g.setColor(getForeground());
			g.drawString("" + chars[i], x, h);

			x += w;
		}
	}
}