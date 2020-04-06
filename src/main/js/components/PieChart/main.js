import React from "react"
import CanvasJSChart from "react-charts"
import { useSelector } from "react-redux";

const options = (title, query) => {

    const [dataPoints] = query();
    return {
        
        animationEnabled: true,
        exportEnabled: true,
        theme: "dark2", 
        title:{
            text: title
        },
        data: [{
            type: "pie",
            indexLabel: "{label}: {y}%",		
            startAngle: -90,
            dataPoints: [
                dataPoints
                // { y: 20, label: "Airfare" },
                // { y: 24, label: "Food & Drinks" },
                // { y: 20, label: "Accomodation" },
                // { y: 14, label: "Transportation" },
                // { y: 12, label: "Activities" },
                // { y: 10, label: "Misc" }	
            ]
        }]
    }
}

const PieChart = (props) => {
    
    const params = useSelector(state => state.params);
    
    
    return(
        <div>
        <CanvasJSChart options = {options(props.title, dataPoints)} />
        {/* onRef={ref => this.chart = ref} */}
        </div>
        
        )
    }

    export default PieChart;