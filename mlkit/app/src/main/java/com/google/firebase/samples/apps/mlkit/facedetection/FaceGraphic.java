// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.firebase.samples.apps.mlkit.facedetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.google.android.gms.vision.CameraSource;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.samples.apps.mlkit.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.GraphicOverlay.Graphic;
import com.google.firebase.samples.apps.mlkit.R;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceGraphic extends Graphic {
  private static final float FACE_POSITION_RADIUS = 10.0f;
  private static final float ID_TEXT_SIZE = 40.0f;
  private static final float ID_Y_OFFSET = 50.0f;
  private static final float ID_X_OFFSET = -50.0f;
  private static final float BOX_STROKE_WIDTH = 5.0f;
  private static int rectWidth = 0;
  private static int rectHeight = 0;

  private static final int[] COLOR_CHOICES = {
    Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW
  };
  private static int currentColorIndex = 0;

  private int facing;

  private Paint facePositionPaint;
  private Paint idPaint;
  private Paint boxPaint;

  private volatile FirebaseVisionFace firebaseVisionFace;

  public FaceGraphic(GraphicOverlay overlay) {
    super(overlay);

    currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.length;
    final int selectedColor = COLOR_CHOICES[currentColorIndex];

    facePositionPaint = new Paint();
    facePositionPaint.setColor(selectedColor);

    idPaint = new Paint();
    idPaint.setColor(selectedColor);
    idPaint.setTextSize(ID_TEXT_SIZE);

    boxPaint = new Paint();
    boxPaint.setColor(selectedColor);
    boxPaint.setStyle(Paint.Style.STROKE);
    boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
  }

  /**
   * Updates the face instance from the detection of the most recent frame. Invalidates the relevant
   * portions of the overlay to trigger a redraw.
   */
  public void updateFace(FirebaseVisionFace face, int facing) {
    firebaseVisionFace = face;
    this.facing = facing;
    postInvalidate();
  }

  /** Draws the face annotations for position on the supplied canvas. */
  @Override
  public void draw(Canvas canvas) {
    FirebaseVisionFace face = firebaseVisionFace;
    if (face == null) {
      return;
    }

    // Draws a circle at the position of the detected face, with the face's track id below.
    float x = translateX(face.getBoundingBox().centerX());
    float y = translateY(face.getBoundingBox().centerY());
    //canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint);
    //canvas.drawText(" " + face.getTrackingId(), x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint);
    /*canvas.drawText(
        "happiness: " + String.format("%.2f", face.getSmilingProbability()),
        x + ID_X_OFFSET * 3,
        y - ID_Y_OFFSET,
        idPaint);*/
    if (facing == CameraSource.CAMERA_FACING_FRONT) {
      /*canvas.drawText(
          "right eye: " + String.format("%.2f", face.getRightEyeOpenProbability()),
          x - ID_X_OFFSET,
          y,
          idPaint);
      canvas.drawText(
          "left eye: " + String.format("%.2f", face.getLeftEyeOpenProbability()),
          x + ID_X_OFFSET * 6,
          y,
          idPaint);*/
    } else {
      canvas.drawText(
          "left eye: " + String.format("%.2f", face.getLeftEyeOpenProbability()),
          x - ID_X_OFFSET,
          y,
          idPaint);
      canvas.drawText(
          "right eye: " + String.format("%.2f", face.getRightEyeOpenProbability()),
          x + ID_X_OFFSET * 6,
          y,
          idPaint);

    }

    // Draws a bounding box around the face.
    float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
    float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);
    float left = x - xOffset;
    float top = y - yOffset;
    float right = x + xOffset;
    float bottom = y + yOffset;
    /*float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
    float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);
    int xOffInt = Math.round(xOffset);
    int yOffInt = Math.round(yOffset);
    int xInt = Math.round(x);
    int yInt = Math.round(y);
    int left = xInt - xOffInt;
    int top = yInt - yOffInt;
    int right = xInt + xOffInt;
    int bottom = yInt + yOffInt;

    Rect rectangle = new Rect(left, top, right, bottom);
    rectWidth = rectangle.width();
    rectHeight = rectangle.height();
    canvas.drawRect(rectangle, boxPaint);*/
    //canvas.drawRect(left, top, right, bottom, boxPaint);

    // draw landmarks
    /*drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.BOTTOM_MOUTH);
    drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.LEFT_CHEEK);
    drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.LEFT_EAR);
    drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.LEFT_MOUTH);
    //drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.LEFT_EYE);
    drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.NOSE_BASE);
    drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.RIGHT_CHEEK);
    drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.RIGHT_EAR);
    //drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.RIGHT_EYE);
    drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.RIGHT_MOUTH);*/
    drawGlasses(canvas, face, FirebaseVisionFaceLandmark.NOSE_BASE);
  }

  private void drawLandmarkPosition(Canvas canvas, FirebaseVisionFace face, int landmarkID) {
    FirebaseVisionFaceLandmark landmark = face.getLandmark(landmarkID);
    if (landmark != null) {
      FirebaseVisionPoint point = landmark.getPosition();
      canvas.drawCircle(
              translateX(point.getX()),
              translateY(point.getY()),
              10f, idPaint);
    }
  }
  private void drawGlasses(Canvas canvas, FirebaseVisionFace face, int landmarkID) {
    Matrix matrix = new Matrix();

    FirebaseVisionFaceLandmark landmark = face.getLandmark(landmarkID);
    if (landmark != null) {
      FirebaseVisionPoint point = landmark.getPosition();
      Bitmap gafas = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.blue_normal_op);
      Bitmap resized = Bitmap.createScaledBitmap(gafas, face.getBoundingBox().width()-(face.getBoundingBox().width()/9), face.getBoundingBox().height()/3 - (face.getBoundingBox().height()/12) , true);
      float centreX = (resized.getWidth()) /2;
      float centreY = (resized.getHeight()) /2;

      matrix.setRotate(face.getHeadEulerAngleZ());
      Bitmap rotatedB= Bitmap.createBitmap(resized, 0, 0, resized.getWidth(), resized.getHeight() , matrix, true);
      canvas.drawBitmap( rotatedB, translateX(point.getX() + 0.90f*centreX - face.getHeadEulerAngleZ() ), translateY(point.getY() - 2.25f*centreY - Math.abs(2*face.getHeadEulerAngleZ())),null);
    }
  }
}
