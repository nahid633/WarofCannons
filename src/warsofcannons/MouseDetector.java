/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package warsofcannons;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

public class MouseDetector extends MouseAdapter implements MouseMotionListener {

	int x = 0;
	int y = 0;
	Point pClicked = new Point(0, 0);
	Point pMoved = new Point(0, 0);
        boolean isClicked=false;
	Gameboard game;

	public MouseDetector(Gameboard game) {
		game.addMouseMotionListener(this);
		game.addMouseListener(this);
		this.game = game;

	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		pMoved = e.getPoint();
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseClicked(MouseEvent e) {
//		if (game.play) {
//			if (SwingUtilities.isLeftMouseButton(e))
//				pClicked = e.getPoint();
//		} else {
                      //  game.listenForServerRequest();
			if (SwingUtilities.isLeftMouseButton(e))
				game.addBullet(1);
	}
}
