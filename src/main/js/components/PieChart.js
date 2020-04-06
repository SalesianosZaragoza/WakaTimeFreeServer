import { Doughnut } from "react-chartjs-2"
import React, { useState, useEffect } from 'react';
const PieChart = ({title, query}) => {
    
    let dataPoints = [];
    const [dataset, setDataset] = useState({});
    async function fetchMyAPI() {
        const data = query.then(promise => {
            return promise.data;
        })
        console.log(data)
        dataPoints=await Promise.resolve(data);
      }
    useEffect(() => {
        fetchMyAPI();
        console.log("dataPoints"+dataPoints);
        console.log("vacio");
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
    }, [])
        return(
            <div>
                <Doughnut  id={title} data = {dataset} />
                <center>{title}</center>
            </div>
            
            )
        }
        
        export default PieChart;