/**
 * @author Jelle 387615
 * Plot the occurrences of the class label for a given attribute in a dataset
 * @param data Map<Class label, AttributeMap<Attribute name, LabelMap<Label, Label count>>>
 * @param attribute Selected attribute of which the occurrence of each label should be plotted
 * @param classLabel Class label of the dataset
 */
plotData = function (data, attribute, classLabel){
    let parsedData = JSON.parse(data);

    // If requested attribute is not the class attribute split the data by class label
    // this creates a stacked barchart
    if (attribute !== classLabel){
        let plotData = [];
        let layout = {barmode: 'stack'}
        // for every class label, count the occurrence of the labels of the requested attribute
        for (let key in parsedData){
            let trace = {
                x : [],
                y : [],
                name: '',
                type: 'bar'
            };
            if (parsedData.hasOwnProperty(key)){
                trace["name"] = key;
                trace["x"] = Object.keys(parsedData[key][attribute]);
                trace["y"] = Object.values(parsedData[key][attribute]);
            }
            plotData.push(trace);
        }
        Plotly.newPlot('plot', plotData, layout);
    }

        // If the requested attribute is the same as the class attribute only plot the class label count
    // This creates a regular barchart with no split
    else {
        let trace = {
            x: [],
            y: [],
            name: '',
            type: 'bar'
        }
        // for every class label, count its occurrence.
        for (let key in parsedData){
            if (parsedData.hasOwnProperty(key)){
                trace["x"].push(key);
                let attrs = Object.keys(parsedData[key]);
                let totalCount = 0;
                let counts = Object.values(parsedData[key][attrs[0]]);
                for (let u = 0; u < counts.length; u++){
                    totalCount += counts[u];
                }
                trace["y"].push(totalCount);
            }
        }
        Plotly.newPlot("plot", [trace]);
    }
}