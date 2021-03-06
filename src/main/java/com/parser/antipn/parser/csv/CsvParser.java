package com.parser.antipn.parser.csv;

import com.parser.antipn.parser.exception.ErrorRowCatcher;
import com.parser.antipn.parser.iodata.InputDataRow;
import com.parser.antipn.parser.iodata.OutputDataRow;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

//основной класс обработки непосредственно самих строк из файлов
public class CsvParser {
    public static void main(String[] args) throws Exception {

    }

    public InputDataRow parseRow(String inputRow) {// parsing one row -> InputDataRow
        InputDataRow inputDataRow = new InputDataRow();
        if (inputRow == null || inputRow.equals("")) {
            throw new ErrorRowCatcher("Input row is null or blank");
        }
        String[] splitedRow = inputRow.split(",");
        for (int i = 0; i < splitedRow.length; i++) {  //checking length of parameters
            if (splitedRow[i].length() == 0) {
                throw new ErrorRowCatcher("Empty " + (i + 1) + " parameter of row");
            }
        }
        if (splitedRow.length != 4) { //checking count of parameters
            throw new ErrorRowCatcher("Incorrect row format");
        }
        try {//setting data to output format
            inputDataRow.setId(Integer.parseInt(splitedRow[0])); //id
            inputDataRow.setSum(Double.parseDouble(splitedRow[1])); //sum
            inputDataRow.setCurrency(splitedRow[2]); //currency
            inputDataRow.setDescription(splitedRow[3]); //description
            return inputDataRow;
        } catch (Exception e) {

            //формируем строку с описанием ошибки как сказано в задании

            throw new ErrorRowCatcher(e.getMessage()); //сам текст ошибки
        }
    }

    public List<CsvInputDataRow> parseRows(List<String> inputRows) {//processing list of rows -> list of CsvInputDataRow

        // standard checking
        if (inputRows == null) { // Спросить Сашу нужно ли тут кидать исключение
            System.out.println("Input list is null");
            return null;
        }
        if (inputRows.isEmpty()) { // Спросить Сашу нужно ли тут кидать исключение
            System.out.println("Input list is empty");
            return null;
        }
        List<CsvInputDataRow> result = new ArrayList<>();

        for (int i = 0; i < inputRows.size(); i++) {
            CsvInputDataRow csvInputDataRow = new CsvInputDataRow(); // data line result
            try {
                InputDataRow inputDataRow = parseRow(inputRows.get(i)); //trying parsing one row from list of rows
                csvInputDataRow.setData(inputDataRow); //adding processed row
                csvInputDataRow.setLine(i + 1); //adding row number
                if (inputDataRow != null) { //if succeed parsing -> OK
                    csvInputDataRow.setResult("OK");
                }
            } catch (ErrorRowCatcher e) { //if parsing failed
                System.out.println(e.getMessage());
                csvInputDataRow.setData(null);// wrong row will be replaced by null but we need add wrong row for future output with NOK status
                csvInputDataRow.setLine(i + 1);
                csvInputDataRow.setResult("NOK " + e.getMessage());
            }
            result.add(csvInputDataRow);
        }
        return result;
    }

    public List<String> readFile(String path) {//reading file and putting rows in list
        Path pathFile = Paths.get(path);
        boolean exists = Files.exists(pathFile);
        if (exists == true) {
            try {
                return Files.readAllLines(pathFile);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("There is problem with file " + pathFile.getFileName());
            }
        }
        return null;
    }

    public CsvInputDataFile parseCsv(String fileName) {    // parsing csv: fileName - > CsVInputDataFile
        CsvInputDataFile csvInputDataFile = new CsvInputDataFile();
        try {
            csvInputDataFile.setData(parseRows(readFile(fileName)));
            csvInputDataFile.setFileName(fileName);
            return csvInputDataFile;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
