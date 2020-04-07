import { Doughnut } from "react-chartjs-2"
import React, { useState, useContext, useEffect } from 'react';
import { store } from "../StateProvider"
const PieChart = ({title, url}) => {
    const [dataset, setDataset] = useState({});
    dataset && console.log(dataset);
    const colors = [];
    const chartQuery = async(urlString, topic, tokenId, from, to) => {
        var url = new URL(urlString+"Chart/");
        tokenId && url.searchParams.append("tokenId" , tokenId);
        topic && url.searchParams.append("topic" , topic);
        from && url.searchParams.append("from" , from);
        to && url.searchParams.append("to" , to);
        fetch(url).then(response => response.json())
        .then(data => 
            {   
                if(data===undefined) return;
                data && data.label && data.label.forEach(element => {
                    colors.push('#'+(Math.random()*0xFFFFFF<<0).toString(16));
                });
                const dataset = {
                    labels: data.label || [],
                    datasets: [{
                        data: data.value || [],
                        backgroundColor: colors
                    }] 
                };
                setDataset(dataset);
            }    
            );
    };    
    const context = useContext(store);
    async function fetchMyAPI() {
        const state = context.state;
        chartQuery(url, title,state.tokenId, state.from, state.to);
    }
    useEffect(() => { 
        fetchMyAPI();
    }, [context.state])
    return(
        <div>
            <Doughnut  id={title} data = {dataset} />
            <center>{title}</center>
        </div>
        )
    } 
    export default PieChart;