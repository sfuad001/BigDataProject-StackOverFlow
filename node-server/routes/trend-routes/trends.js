var express = require('express');
var router = express.Router();
const customUtils = require('../../utils/utils');
const path = require('path');
const fs = require('fs');

/* GET post count vs location. */
router.get('/trends/particular-year-trend', function (req, res, next) {
    const dirPath = path.join(process.cwd(), "outputs", "trends");
    let yearFileList = [];
    let languageFileList = [];
    let jsonDataYearList = [];
    let jsonDataLanguageList = [];
    try {
        yearFileList = fs.readdirSync(path.join(dirPath, "year"));
        languageFileList = fs.readdirSync(path.join(dirPath, "language"));

        for (let i = yearFileList.length - 1; i >= 0; i--) {
            const filename = yearFileList[i];
            const csvFilePath = path.join(dirPath, "year", filename);
            console.log(csvFilePath);
            const csvData = customUtils.readCSV(csvFilePath);
            const jsonList = customUtils.convertCSVtoJson(csvData);
            const year = filename.split(".")[0];
            const jsonData = {};
            jsonData.year = year;
            jsonData.jsonList = jsonList;
            jsonDataYearList.push(jsonData);
        }

        languageFileList.forEach(filename => {
            const csvFilePath = path.join(dirPath, "language", filename);
            console.log(csvFilePath);
            const csvData = customUtils.readCSV(csvFilePath);
            const jsonList = customUtils.convertCSVtoJson(csvData);
            const language = filename.split(".")[0];
            const jsonData = {};
            jsonData.language = language;
            jsonData.jsonList = jsonList;
            jsonDataLanguageList.push(jsonData);
        });
    } catch (err) {
        console.log(err);
    }

    console.log(jsonDataLanguageList);
    console.log(jsonDataYearList);

    return res.send({
        "msg": "Success",
        "jsonDataYearList": jsonDataYearList,
        "jsonDataLanguageList": jsonDataLanguageList 
    });
});

module.exports = router;
