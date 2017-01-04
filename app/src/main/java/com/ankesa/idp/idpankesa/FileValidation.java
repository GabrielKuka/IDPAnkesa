package com.ankesa.idp.idpankesa;

// This class will mark the file that the user has selected as valid or not

class FileValidation {

    private  boolean isFileValidated = false;

    FileValidation(boolean v) {
        isFileValidated = v;
    }

     boolean getFileValidated() {
        return isFileValidated;
    }

    void setFileValidated(boolean v) {
        isFileValidated = v;
    }

}
