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
            <PieChart title={Categories.LANGUAGE} query={context.state.query(Categories.LANGUAGE)} />
            <PieChart title={Categories.FILENAME} query={context.state.query(Categories.FILENAME)}/>
            <PieChart title={Categories.BRANCH} query={context.state.query(Categories.BRANCH)}/>
            <PieChart title={Categories.PROJECT} query={context.state.query(Categories.PROJECT)}/>
        </>
        );
    }
    export default Desktop;
    