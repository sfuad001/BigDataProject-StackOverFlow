var express = require('express');
var router = express.Router();
const customUtils = require('../../utils/utils');
const path = require('path');

/* GET post count vs location. */
router.get('/location/post-count-location-sorted', function(req, res, next) {
    const csvFilePath = path.join(process.cwd(), "outputs", "location", "CountWise_sorted.csv");
    console.log(csvFilePath);
    const csvData = customUtils.readCSV(csvFilePath);
    const jsonData = customUtils.convertCSVtoJson(csvData);

    return res.send({
        "msg": "Success",
        "jsonData": jsonData
    })
});

module.exports = router;
