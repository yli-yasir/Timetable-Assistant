//
//
//    private FileChooser makeSaveDialog(){
//        FileChooser saveDialog = new FileChooser();
//        saveDialog.getExtensionFilters().add(
//                new FileChooser.ExtensionFilter("PNG", "*.png"));
//        return saveDialog;
//    }
//
//    /*Since we are saving, there should be only one extension in the list, so
//        we just get the one at the first index, and extensions should be specified as
//        *.<extension> so we trim the in string starting from the second index until the end
//        to get a string extension which can be used in saving the file*/
//    private String getExtension(FileChooser saveDialog){
//        return saveDialog.getSelectedExtensionFilter().getExtensions()
//                .get(0).substring(2);
//    }
//
//    private void saveImage(BufferedImage image,File file,String extension){
//        try{
//            ImageIO.write(image,extension,file);
//        }
//        catch(IOException e ){
//            e.printStackTrace();
//        }
//
//    }
//
//}
