import PieChart from "./PieChart"
import InputTable from "./InputTable"
import React, { useContext } from 'react';
import { store } from "../StateProvider"
const Categories = { BRANCH : "branch",  PROJECT : "project", LANGUAGE : "language", FILENAME : "filename"}
Object.freeze(Categories)
const Desktop = (props) => {
    const context = useContext(store); 
    return (
        <>
            <InputTable />
            <PieChart title={Categories.LANGUAGE} url={context.state.url} />
            <PieChart title={Categories.FILENAME} url={context.state.url}/>
            <PieChart title={Categories.BRANCH} url={context.state.url}/>
            <PieChart title={Categories.PROJECT} url={context.state.url}/>
        </>
        );
    }
    export default Desktop;
    