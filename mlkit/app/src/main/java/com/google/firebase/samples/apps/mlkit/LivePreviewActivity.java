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
package com.google.firebase.samples.apps.mlkit;

import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.samples.apps.mlkit.facedetection.FaceGraphic;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.common.annotation.KeepName;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.samples.apps.mlkit.barcodescanning.BarcodeScanningProcessor;
import com.google.firebase.samples.apps.mlkit.custommodel.CustomImageClassifierProcessor;
import com.google.firebase.samples.apps.mlkit.facedetection.Adapter;
import com.google.firebase.samples.apps.mlkit.facedetection.FaceDetectionProcessor;
import com.google.firebase.samples.apps.mlkit.facedetection.Objetos.Glasses;
import com.google.firebase.samples.apps.mlkit.imagelabeling.ImageLabelingProcessor;
import com.google.firebase.samples.apps.mlkit.textrecognition.TextRecognitionProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Demo app showing the various features of ML Kit for Firebase. This class is used to
 * set up continuous frame processing on frames from a camera source. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
    implements OnRequestPermissionsResultCallback,
        OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener{
  private static final String FACE_DETECTION = "Face Detection";
  private static final String TEXT_DETECTION = "Text Detection";
  private static final String BARCODE_DETECTION = "Barcode Detection";
  private static final String IMAGE_LABEL_DETECTION = "Label Detection";
  private static final String CLASSIFICATION = "Classification";
  private static final String TAG = "LivePreviewActivity";
  private static final int PERMISSION_REQUESTS = 1;

  private CameraSource cameraSource = null;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private String selectedModel = FACE_DETECTION;

  private volatile FirebaseVisionFace firebaseVisionFace;
  private List<Glasses> glassesList = new ArrayList<>();
  private RecyclerView glassesRecyclerView;
  private Adapter glassesAdapter;
  FaceGraphic fg = new FaceGraphic(graphicOverlay);




  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.activity_live_preview);
    buildRecycler();

    glassesAdapter = new Adapter(glassesList, getApplicationContext(), new Adapter.OnItemClickListener() {
      @Override
      public void onItemClick(Glasses gafa) {
        Log.i("click", "has clickeado");
        int resID = gafa.productImage;
        Log.i("Huevos", "h"+resID);
        fg.drawMierda(resID);
      }
    });
    glassesRecyclerView.setAdapter(glassesAdapter);

    preview = (CameraSourcePreview) findViewById(R.id.firePreview);
    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = (GraphicOverlay) findViewById(R.id.fireFaceOverlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }

    //Spinner spinner = (Spinner) findViewById(R.id.spinner);
    List<String> options = new ArrayList<>();
/*    options.add(FACE_DETECTION);
    options.add(TEXT_DETECTION);
    options.add(BARCODE_DETECTION);
    options.add(IMAGE_LABEL_DETECTION);
    options.add(CLASSIFICATION);*/
    // Creating adapter for spinner
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);
    // Drop down layout style - list view with radio button
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // attaching data adapter to spinner
    //spinner.setAdapter(dataAdapter);
    //spinner.setOnItemSelectedListener(this);

    /*ToggleButton facingSwitch = (ToggleButton) findViewById(R.id.facingswitch);
    facingSwitch.setOnCheckedChangeListener(this);*/

    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
    } else {
      getRuntimePermissions();
    }


    populateglassesList();

  }

  public void buildRecycler(){
    Log.i("click", "has clickeado");
    FirebaseVisionFace face = firebaseVisionFace;
    glassesRecyclerView = findViewById(R.id.idRecyclerViewHorizontalList);
    // add a divider after each item for more clarity
    glassesRecyclerView.addItemDecoration(new DividerItemDecoration(LivePreviewActivity.this, LinearLayoutManager.HORIZONTAL));
    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(LivePreviewActivity.this, LinearLayoutManager.HORIZONTAL, false);
    glassesRecyclerView.setLayoutManager(horizontalLayoutManager);
  }

  private void populateglassesList(){

    Glasses redondas = new Glasses("Redondas", R.drawable.redondas);
    Glasses blue_hawkers = new Glasses("Azules", R.drawable.blue_normal_op);
    Glasses black_hawkers = new Glasses("Negras", R.drawable.gafas_reales);
    Glasses rayban_chulito = new Glasses("Rayban", R.drawable.rayban);
    Glasses oculus = new Glasses("Oculusrift", R.drawable.oculusrift);
    glassesList.add(redondas);
    glassesList.add(blue_hawkers);
    glassesList.add(black_hawkers);
    glassesList.add(rayban_chulito);
    glassesList.add(oculus);
    glassesAdapter.notifyDataSetChanged();
  }

  @Override
  public synchronized void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)
    selectedModel = parent.getItemAtPosition(pos).toString();
    Log.d(TAG, "Selected model: " + selectedModel);
    preview.stop();
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
      startCameraSource();
    } else {
      getRuntimePermissions();
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    Log.d(TAG, "Set facing");
    if (cameraSource != null) {
      if (isChecked) {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
      } else {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
      }
    }
    preview.stop();
    startCameraSource();
  }

  private void createCameraSource(String model) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }

    try {
      switch (model) {
        case CLASSIFICATION:
          Log.i(TAG, "Using Custom Image Classifier Processor");
          cameraSource.setMachineLearningFrameProcessor(new CustomImageClassifierProcessor(this));
          break;
        case TEXT_DETECTION:
          Log.i(TAG, "Using Text Detector Processor");
          cameraSource.setMachineLearningFrameProcessor(new TextRecognitionProcessor());
          break;
        case FACE_DETECTION:
          Log.i(TAG, "Using Face Detector Processor");
          cameraSource.setMachineLearningFrameProcessor(new FaceDetectionProcessor());
          break;
        case BARCODE_DETECTION:
          Log.i(TAG, "Using Barcode Detector Processor");
          cameraSource.setMachineLearningFrameProcessor(new BarcodeScanningProcessor());
          break;
        case IMAGE_LABEL_DETECTION:
          Log.i(TAG, "Using Image Label Detector Processor");
          cameraSource.setMachineLearningFrameProcessor(new ImageLabelingProcessor());
          break;
        default:
          Log.e(TAG, "Unknown model: " + model);
      }
    } catch (FirebaseMLException e) {
      Log.e(TAG, "can not create camera source: " + model);
    }
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private void startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null");
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null");
        }
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    startCameraSource();
  }

  /** Stops the camera. */
  @Override
  protected void onPause() {
    super.onPause();
    preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
  }

  private String[] getRequiredPermissions() {
    try {
      PackageInfo info =
          this.getPackageManager()
              .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
      String[] ps = info.requestedPermissions;
      if (ps != null && ps.length > 0) {
        return ps;
      } else {
        return new String[0];
      }
    } catch (Exception e) {
      return new String[0];
    }
  }

  private boolean allPermissionsGranted() {
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        return false;
      }
    }
    return true;
  }

  private void getRuntimePermissions() {
    List<String> allNeededPermissions = new ArrayList<>();
    for (String permission : getRequiredPermissions()) {
      if (!isPermissionGranted(this, permission)) {
        allNeededPermissions.add(permission);
      }
    }

    if (!allNeededPermissions.isEmpty()) {
      ActivityCompat.requestPermissions(
          this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
    }
  }

  @Override
  public void onRequestPermissionsResult(
          int requestCode, String[] permissions, int[] grantResults) {
    Log.i(TAG, "Permission granted!");
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private static boolean isPermissionGranted(Context context, String permission) {
    if (ContextCompat.checkSelfPermission(context, permission)
        == PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "Permission granted: " + permission);
      return true;
    }
    Log.i(TAG, "Permission NOT granted: " + permission);
    return false;
  }
}
