var express = require('express');
var router = express.Router();
const customUtils = require('../../utils/utils');
const path = require('path');

/* GET post count vs location. */
router.get('/trends/particular-year-trend', function(req, res, next) {
    const fileName = req.query.fileName;
    const csvFilePath = path.join(process.cwd(), "outputs", "trends", fileName);
    console.log(csvFilePath);
    const csvData = customUtils.readCSV(csvFilePath);
    const jsonData = customUtils.convertCSVtoJson(csvData);
    const year = fileName.split(".")[0];

    return res.send({
        "msg": "Success",
        "year": year,
        "jsonData": jsonData
    })
});

module.exports = router;
