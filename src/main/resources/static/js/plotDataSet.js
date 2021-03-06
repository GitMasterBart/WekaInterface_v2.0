/**
 * @author Jelle 387615
 * Plot the occurrences of the class label for a given attribute in a dataset
 * @param data Map<Class label, AttributeMap<Attribute name, LabelMap<Label, Label count>>>
 * @param attribute Selected attribute of which the occurrence of each label should be plotted
 * @param classLabel Class label of the dataset
 */
plotData = function (data, attribute, classLabel){
    let parsedData = JSON.parse(data);

    if (attribute !== classLabel){
        plotNonClassAttribute(parsedData, attribute)
    }

    else {
        plotClassLabel(parsedData)
    }
}

/**
 * Checks whether or not the given string is a date
 * @param dateStr
 * @returns {boolean}
 */
function isDate(dateStr) {
    return !isNaN(new Date(dateStr).getDate());
}

/**
 * Plot attributes that are not the class attribute.
 * TODO Internationalize the plot layout
 * @param data JSON object
 * @param attribute attribute name
 */
plotNonClassAttribute = function (data, attribute){

    let attrIsDate = false;
    let plotData = [];
    // unsorted list of dates
    let dateObject = {};
    // list of dates that will be sorted
    let sortedDateObject = {};

    for (let classLabel in data){
        let attributeObject = data[classLabel][attribute];
            dateObject[classLabel] = {};
            for(let key in attributeObject){
                if (isDate(new Date(key))){
                    // make the object keys actual dates instead of strings
                    dateObject[classLabel][new Date(key)] = attributeObject[key];
                    attrIsDate = true;
                }
            }

            let trace;
            if (attrIsDate){
                trace = traceNonClassAttributeDate(dateObject, classLabel, sortedDateObject);
            }
            else {
                trace = traceNonClassAttributeNominal(attributeObject, classLabel);
            }
            plotData.push(trace);

    }
    let layout = {  barmode: 'stack',
                    title: {
                        text: "Occurrence of " + attribute + " in the dataset"
                    },
                    xaxis: {
                        title: attribute
                    },
                    yaxis: {
                        title: "Occurrence of " + attribute
                    }}
    Plotly.newPlot('plot', plotData, layout);
}

/**
 * Returns the plot trace for a Date attribute that is not the class attribute.
 * @param dateObject unsorted object with dates as keys and occurrences as value
 * @param classLabel string
 * @param sortedDateObject object with dates as keys and occurrences as values that is to be sorted
 * @returns {{x: *[], name, y: *[], type: string}}
 */
traceNonClassAttributeDate = function (dateObject, classLabel, sortedDateObject){

    let trace = createEmptyBarTrace();
    trace.name = classLabel;

    let sortedDateKeys = Object.keys(dateObject[classLabel]).sort(
        (a, b) => new Date(a) - new Date(b));

    sortedDateObject[classLabel] = {};

    for (let index in sortedDateKeys){
        let date = sortedDateKeys[index];
        sortedDateObject[classLabel][date] = dateObject[classLabel][date];
    }

    for (let key in sortedDateObject[classLabel]){
        if (dateObject[classLabel][key] !== 0){
            trace.x.push(key.split(" ").splice(1, 3).join(" "));
            trace.y.push(dateObject[classLabel][key]);
        }
    }
    return trace;
}

/**
 * Returns the plot trace for a non-Date attribute that is not the class attribute.
 * If the attribute is numeric with an interval as its label, the x-axis is sorted from lowest to highest interval.
 * @param attributeObject
 * @param classLabel
 * @returns {{x: *[], name, y: *[], type: string}}
 */
traceNonClassAttributeNominal = function (attributeObject, classLabel){

    let trace = createEmptyBarTrace();
    trace.name = classLabel;

    let sortedAttributeObject = {};
    let keysToSort = [];

    try { // if the label is an interval sort it from lowest to highest.
        for (let key in attributeObject){
            keysToSort.push(key);
        }

        keysToSort.sort((a, b) => a.split("-")[0] - b.split("-")[0]);

        for (let sortedKey in keysToSort){
            sortedKey = keysToSort[sortedKey];
            sortedAttributeObject[sortedKey] = attributeObject[sortedKey];
        }

        for (let entry in sortedAttributeObject){
            if (sortedAttributeObject[entry] !== 0){
                trace.x.push(entry);
                trace.y.push(sortedAttributeObject[entry]);
            }
        }
    }catch (err){
        for (let entry in attributeObject){
            trace.x.push(entry);
            trace.y.push(attributeObject[entry]);
        }
    }
    return trace;
}

/**
 * Plots the occurrence of each class label.
 * @param data
 */
plotClassLabel = function (data){
    let trace = createEmptyBarTrace();
    // for every class label, count its occurrence.
    for (let key in data){
        if (data.hasOwnProperty(key)){
            trace["x"].push(key);
            let attrs = Object.keys(data[key]);
            let totalCount = 0;
            let counts = Object.values(data[key][attrs[0]]);
            for (let u = 0; u < counts.length; u++){
                totalCount += counts[u];
            }
            trace["y"].push(totalCount);
        }
    }
    Plotly.newPlot("plot", [trace]);
}

/**
 * Plots a dataset with only two attributes
 * TODO Add layout for the plot I.E. title and x/y labels etc.
 * @param data
 */
plotTwoAttributes = function (data){
    let parsedData = JSON.parse(data);
    let listToSort = []

    for (let key in parsedData){
        if (parsedData.hasOwnProperty(key)){
            listToSort.push({date: new Date(key),
            value: parsedData[key]});
        }
    }
    let trace = {
        x: [],
        y: [],
        mode: 'lines'
    }
    listToSort = listToSort.sort((a, b) => a.date - b.date)
    for (let index in listToSort){
        trace['x'].push(listToSort[index].date);
        trace['y'].push(listToSort[index].value);
    }
    Plotly.newPlot("plotTwoAttributes", [trace])
}

/**
 * Returns an empty plotly trace object
 */
createEmptyBarTrace = function (){
    return {
        x: [],
        y: [],
        name: '',
        type: 'bar'
    };
}