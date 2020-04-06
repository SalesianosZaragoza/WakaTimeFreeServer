import { Doughnut } from "react-chartjs-2"
import React, { useState, useContext, useEffect } from 'react';
import { store } from "../StateProvider"
const PieChart = ({title, url}) => {

    const chartQuery = (url, topic, tokenId, from, to) => {
        var url = new URL(url);
        tokenId && url.searchParams.append("tokenId" , tokenId);
        topic && url.searchParams.append("topic" , topic);
        from && url.searchParams.append("from" , from);
        to && url.searchParams.append("to" , to);
        console.log(url);
        return fetch(url) 
    };
    let dataPoints = [];
    const [dataset, setDataset] = useState({});
    const context = useContext(store);
    async function fetchMyAPI() {
        const state = context.state;
        chartQuery(url, title,state.tokenId, state.from, state.to).then(promise => {
            dataPoints = promise.data;
        });
        console.log(dataPoints)
    }
    useEffect(() => {
        fetchMyAPI();
        console.log("actualizando"+dataPoints);
        let labels = []
        dataPoints.forEach(element => {
            labels.push(element.label)
        });
        
        let data = []
        dataPoints.forEach(element => {
            data.push(element.y)
        });
        
        const dataset = {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: [
                    '#FF6384',
                    '#36A2EB',
                    '#FFCE56'
                ]
            }]
        };
        
        dataset.labels = labels;
        dataset.datasets[0].data=data;
    }, [context.state,dataPoints])
    return(
        <div>
            <Doughnut  id={title} data = {dataset} />
            <center>{title}</center>
        </div>
        
        )
    }
    
    export default PieChart;