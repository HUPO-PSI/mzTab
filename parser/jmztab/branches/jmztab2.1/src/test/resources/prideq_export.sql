
--Query Pride-Q database, and export resultset to mzTab
SELECT p.psms_sequence,
       group_concat(concat(m.mods_location, "-", m.mods_main_accession)),
       round(p.psms_prec_mz, 2),
       p.psms_prec_z,
       p.psms_score_peptide,
       p.psms_score_CLUSTER_VALUE,
	   p.psms_accession,
       case p.psms_prideq_rating when 1 then '3' when 3 then '1' when 2 then '2' when 0 then 'null' end,
       p.psms_score_MASCOT_SCORE,
       p.psms_score_XTANDEM_HYPER_SCORE,
       p.psms_score_XTANDEM_EXPECTANCY_SCORE,
       p.psms_score_SEQUEST_SCORE,
       p.psms_score_X_CORRELATION,
       p.psms_score_DELTA_CN,
       p.psms_score_SPECTRUM_MILL_PEPTIDE_SCORE,
       p.psms_score_OMSSA_E_VALUE,
       p.psms_score_OMSSA_P_VALUE
  FROM prideq_psms p, prideq_mods m
  WHERE p.psms_id = m.mods_psms_id
  GROUP BY psms_id limit 20,40; 


-- Query Pride2 database, and create connection between project accession and pmid List.
SELECT e.accession as accession, p.accession as pmid
FROM 
    pride.pride_reference_exp_link l, 
    pride.pride_reference_param p, 
    pride.pride_experiment e
WHERE 
    p.parent_element_fk = l.reference_id 
AND e.experiment_id = l.experiment_id
AND l.public_flag = 1
AND p.cv_label='PubMed'
ORDER BY 1;